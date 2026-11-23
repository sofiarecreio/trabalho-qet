#!/usr/bin/env bash
# Executa cada classe separadamente e preserva os resultados de comandos que falham.
set -uo pipefail
cd "$(dirname "$0")/.." || exit 1
mkdir -p docs/entrega2/consolidacao/logs
java_version="$(mvn -Dstyle.color=never -version 2>&1 | awk '/Java version:/ {gsub(/,/, "", $3); print $3}')"
if [[ "$java_version" != 21.* ]]; then
    printf 'Selecione JDK 21 no Maven. Versão encontrada: %s\n' "$java_version" >&2
    exit 1
fi
: > docs/entrega2/consolidacao/logs/estados.tsv
status=0
for classe in gameloader game movelist timecontrol movegenerator; do
    argumentos=(-B -f chesslib/pom.xml)
    if [ "$classe" != movegenerator ]; then
        argumentos+=("-P${classe}-quality")
    fi
    sufixo="-${classe}"
    if [ "$classe" = movegenerator ]; then
        sufixo=""
    fi
    for etapa in coverage mutation; do
        if [ "$etapa" = coverage ]; then
            rm -f "chesslib/target/jacoco${sufixo}.exec" "chesslib/target/site/jacoco${sufixo}/jacoco.xml"
            metas=(-Dmaven.javadoc.skip=true verify)
        else
            rm -f "chesslib/target/pit-reports${sufixo}/mutations.xml"
            metas=(-Djacoco.skip=true test-compile org.pitest:pitest-maven:1.17.4:mutationCoverage)
        fi
        arquivo="docs/entrega2/consolidacao/logs/${classe}-${etapa}.log"
        printf '%s — %s\n' "$classe" "$etapa"
        if mvn "${argumentos[@]}" "${metas[@]}" > "$arquivo" 2>&1; then
            printf 'Comando concluído: %s\n' "$arquivo"
            codigo=0
        else
            printf 'Falhou; consultar: %s\n' "$arquivo"
            codigo=1
            status=1
        fi
        printf '%s\t%s\t%s\n' "$classe" "$etapa" "$codigo" >> docs/entrega2/consolidacao/logs/estados.tsv
    done
done
python3 scripts/consolidar-metricas.py --java-version "$java_version" \
    --run-statuses docs/entrega2/consolidacao/logs/estados.tsv --check-thresholds || exit 1
exit "$status"
