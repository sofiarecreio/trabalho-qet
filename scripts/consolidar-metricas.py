"""Consolida relatórios JaCoCo/PIT existentes, preservando os estados de mutação."""
import argparse
import csv
import hashlib
import json
import shutil
import subprocess
import xml.etree.ElementTree as ET
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CLASSES = [
    ("GameLoader", "gameloader", "Sofia", "pgn"),
    ("Game", "game", "Weslley", "game"),
    ("MoveList", "movelist", "Lucio", "move"),
    ("TimeControl", "timecontrol", "Pedro", "game"),
    ("MoveGenerator", "", "Lucas", "move"),
]



def render_tables(output, results):
    """Atualiza apenas as tabelas, preservando as decisões e instruções do relatório."""
    report_path = output / "README.md"
    if not report_path.exists():
        return
    report = report_path.read_text(encoding="utf-8")
    percent = lambda numerator, denominator: (f"{numerator / denominator * 100:.2f}".replace(".", ",") + "%")
    table = ["| Responsável | Classe | Complexidade agregada JaCoCo | Branches | Mutantes mortos / total | Situação nas metas de 80% |",
             "| --- | --- | ---: | ---: | ---: | --- |"]
    for item in results:
        branch = item["counters"]["BRANCH"]
        total_branches = branch["covered"] + branch["missed"]
        killed = item["mutation_statuses"].get("KILLED", 0)
        total = item["mutation_total"]
        complexity = item["counters"]["COMPLEXITY"]
        status = "Atende"
        if not item["meets_branch_threshold"]:
            status = "Branches abaixo de 80%"
        if not item["meets_strict_mutation_threshold"]:
            status = "Mutação abaixo de 80%"
        if status == "Atende" and any(value for key, value in item["mutation_statuses"].items()
                                      if key not in ("KILLED", "SURVIVED", "NO_COVERAGE")):
            status = "Atende numericamente; ocorrências na execução"
        table.append(f"| {item['owner']} | {item['class']} | {complexity['covered'] + complexity['missed']} | "
                     f"{branch['covered']}/{total_branches} = {percent(branch['covered'], total_branches)} | "
                     f"{killed}/{total} = {percent(killed, total)} | {status} |")
    core = ["KILLED", "SURVIVED", "NO_COVERAGE"]
    statuses = core + sorted({key for item in results for key in item["mutation_statuses"]} - set(core))
    mutation_table = ["| Classe | " + " | ".join(statuses) + " | Total |",
                      "| --- | " + " | ".join(["---:"] * (len(statuses) + 1)) + " |"]
    for item in results:
        values = [str(item["mutation_statuses"].get(status, 0)) for status in statuses]
        mutation_table.append("| " + item["class"] + " | " + " | ".join(values) + f" | {item['mutation_total']} |")
    start = report.index("## Resultados das cinco classes")
    end = report.index("## Critérios e decisões", start)
    report = (report[:start] + "## Resultados das cinco classes\n\n" + "\n".join(table)
              + "\n\n## Estados do PIT\n\n" + "\n".join(mutation_table) + "\n\n" + report[end:])
    report_path.write_text(report, encoding="utf-8")


def consolidate(java_version, run_statuses=None):
    output = ROOT / "docs/entrega2/consolidacao"
    results = []
    for name, suffix, owner, package in CLASSES:
        ending = "-" + suffix if suffix else ""
        coverage_path = ROOT / f"chesslib/target/site/jacoco{ending}/jacoco.xml"
        mutations_path = ROOT / f"chesslib/target/pit-reports{ending}/mutations.xml"
        coverage = ET.parse(coverage_path)
        target = next(item for item in coverage.findall(".//class")
                      if item.get("name", "").endswith("/" + name))
        counters = {
            item.get("type"): {"covered": int(item.get("covered")),
                               "missed": int(item.get("missed"))}
            for item in target.findall("counter")
        }
        methods = [
            {"method": method.get("name"),
             "complexity": int(counter.get("covered")) + int(counter.get("missed"))}
            for method in target.findall("method")
            for counter in method.findall("counter")
            if counter.get("type") == "COMPLEXITY"
        ]
        mutations = ET.parse(mutations_path).findall("mutation")
        if not mutations or any(item.findtext("mutatedClass", "").split(".")[-1] != name
                                for item in mutations):
            raise ValueError(f"Relatório PIT vazio ou fora do escopo: {name}")
        statuses = Counter(item.get("status") for item in mutations)
        source = ROOT / f"chesslib/src/main/java/com/github/bhlangonijr/chesslib/{package}/{name}.java"
        branches = counters["BRANCH"]
        branch_total = branches["covered"] + branches["missed"]
        meets_coverage = branch_total > 0 and branches["covered"] * 100 >= branch_total * 80
        meets_mutation = statuses.get("KILLED", 0) * 100 >= len(mutations) * 80
        results.append({
            "class": name,
            "owner": owner,
            "profile": suffix + "-quality" if suffix else "default",
            "source_sha256": hashlib.sha256(source.read_bytes()).hexdigest(),
            "counters": counters,
            "maximum_method_complexity": max(methods, key=lambda item: item["complexity"]),
            "mutation_statuses": dict(statuses),
            "mutation_total": len(mutations),
            "meets_branch_threshold": meets_coverage,
            "meets_strict_mutation_threshold": meets_mutation,
        })
        destination = output / "evidencias" / (suffix or "movegenerator")
        destination.mkdir(parents=True, exist_ok=True)
        shutil.copy2(coverage_path, destination / "jacoco.xml")
        shutil.copy2(mutations_path, destination / "mutations.xml")
        print(name, "branches:", counters["BRANCH"], "PIT:", dict(statuses))
    base = subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=ROOT, text=True).strip()
    record = {"base_commit": base, "jdk_informado_pelo_executor": java_version,
              "jacoco": "0.8.12", "pit": "1.17.4", "results": results}
    if run_statuses:
        runs = []
        with Path(run_statuses).open(encoding="utf-8") as stream:
            for name, stage, code in csv.reader(stream, delimiter="\t"):
                command = ["mvn", "-B", "-f", "chesslib/pom.xml"]
                if name != "movegenerator":
                    command.append(f"-P{name}-quality")
                command += (["-Dmaven.javadoc.skip=true", "verify"] if stage == "coverage" else
                            ["-Djacoco.skip=true", "test-compile",
                             "org.pitest:pitest-maven:1.17.4:mutationCoverage"])
                runs.append({"class": name, "stage": stage, "exit_code": int(code), "command": command})
        (output / "execucoes.json").write_text(json.dumps(runs, indent=2) + "\n", encoding="utf-8")
    (output / "metricas.json").write_text(
        json.dumps(record, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    render_tables(output, results)
    return all(item["meets_branch_threshold"] and item["meets_strict_mutation_threshold"]
               for item in results)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--java-version", default="não informado",
                        help="Versão efetivamente utilizada nas execuções dos relatórios")
    parser.add_argument("--run-statuses", type=Path, help="TSV com classe, etapa e código de saída")
    parser.add_argument("--check-thresholds", action="store_true",
                        help="Retorna código 2 se alguma classe ficar abaixo de 80%%, sem arredondamento")
    args = parser.parse_args()
    meets_thresholds = consolidate(args.java_version, args.run_statuses)
    if args.check_thresholds and not meets_thresholds:
        print("Consolidação concluída; há classes abaixo da meta exata de 80%.")
        raise SystemExit(2)
