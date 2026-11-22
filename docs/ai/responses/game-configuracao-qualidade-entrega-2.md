# Resposta — Perfil de qualidade de Game

```xml
    <profiles>
        <profile>
            <id>game-quality</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.jacoco</groupId>
                        <artifactId>jacoco-maven-plugin</artifactId>
                        <configuration>
                            <includes combine.self="override">
                                <include>com/github/bhlangonijr/chesslib/game/Game.class</include>
                            </includes>
                            <destFile>${project.build.directory}/jacoco-game.exec</destFile>
                            <dataFile>${project.build.directory}/jacoco-game.exec</dataFile>
                            <outputDirectory>${project.build.directory}/site/jacoco-game</outputDirectory>
                            <append>false</append>
                        </configuration>
                        <executions>
                            <execution>
                                <id>prepare-agent</id>
                                <configuration>
                                    <includes combine.self="override">
                                        <include>com/github/bhlangonijr/chesslib/game/Game</include>
                                    </includes>
                                </configuration>
                            </execution>
                            <execution>
                                <id>check</id>
                                <configuration>
                                    <rules combine.self="override">
                                        <rule>
                                            <element>CLASS</element>
                                            <includes>
                                                <include>com.github.bhlangonijr.chesslib.game.Game</include>
                                            </includes>
                                            <limits>
                                                <limit>
                                                    <counter>BRANCH</counter>
                                                    <value>COVEREDRATIO</value>
                                                    <minimum>0.80</minimum>
                                                </limit>
                                            </limits>
                                        </rule>
                                    </rules>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                    <plugin>
                        <groupId>org.pitest</groupId>
                        <artifactId>pitest-maven</artifactId>
                        <configuration>
                            <targetClasses combine.self="override">
                                <param>com.github.bhlangonijr.chesslib.game.Game</param>
                            </targetClasses>
                            <targetTests combine.self="override">
                                <param>com.github.bhlangonijr.chesslib.game.Game*Test</param>
                            </targetTests>
                            <reportsDirectory>${project.build.directory}/pit-reports-game</reportsDirectory>
                        </configuration>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
```

Execução da cobertura, a partir da raiz do repositório:

```powershell
mvn -B -f chesslib/pom.xml -Pgame-quality "-Dmaven.javadoc.skip=true" clean verify
```

Execução da mutação:

```powershell
mvn -B -f chesslib/pom.xml -Pgame-quality "-Djacoco.skip=true" test-compile org.pitest:pitest-maven:1.17.4:mutationCoverage
```
