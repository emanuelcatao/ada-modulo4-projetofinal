import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.*;


public class Main {
    public static Stream<String> lerCSVAsStream(String arquivo) {
        Path caminho = Paths.get(arquivo);
        try {
            return Files.lines(caminho);
        } catch (IOException e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }

    private static List<String> jogadorComMaisGolsFiltro(Predicate<String> filtro) {
        Stream<String> gols = lerCSVAsStream("src/resources/campeonato-brasileiro-gols.csv");

        Map<String, Long> contagemPorJogador = gols
                .filter(filtro)
                .map(linha -> linha.split(",")[3].trim())
                .collect(Collectors.groupingBy(jogador -> jogador, Collectors.counting()));

        Long maxGols = contagemPorJogador.values().stream().max(Long::compare).orElse(0L);

        return contagemPorJogador.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxGols))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static List<String> jogadorComMaisGols() {
        return jogadorComMaisGolsFiltro(linha -> true);
    }

    public static List<String> jogadorComMaisGolsDePenalti() {
        return jogadorComMaisGolsFiltro(linha -> linha.contains("Penalty"));
    }

   /* public static List<String> estatisticasCampeonato(
            Predicate<String> filtro,
            int coluna,
            String minOuMax
    ) {
        Stream<String> partidas = lerCSVAsStream("src/resources/campeonato-brasileiro-full.csv");

        Map<String, Long> contagemPorTime = partidas
                .filter(filtro)
                .map(linha -> linha.split(",")[coluna].trim())
                .collect(
                        Collectors.groupingBy(
                                time -> time,
                                Collectors.counting()
                        )
                );

        Long vitorias;

        if (minOuMax.equals("max")) {
            vitorias= contagemPorTime
                    .values()
                    .stream()
                    .max(Long::compare)
                    .orElse(0L);
        } else if (minOuMax.equals("min")) {
            vitorias= contagemPorTime
                    .values()
                    .stream()
                    .min(Long::compare)
                    .orElse(0L);
        } else {
            vitorias = 0L;
        }

        return contagemPorTime
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(vitorias))
                .map(Map.Entry::getKey)
                .toList();
    }

    public static List<String> timeComMaisVitorias() {
        return estatisticasCampeonato(
                linha -> linha.contains("2008") && !linha.contains("-"),
                10,
                "max"
        );
    }

    public static List<String> estadoComMenosVitoria() {
        return estatisticasCampeonato(
                linha -> !linha.contains("mandante"),
                14,
                "min"
        );
    }*/

    private static List<String> jogadorComMaisCartoesVermelhos() {
        Stream<String> cartoes = lerCSVAsStream("src/resources/campeonato-brasileiro-cartoes.csv");

        Map<String, Long> contagemPorJogador = cartoes
                .filter(linha -> linha.contains("Vermelho"))
                .map(linha -> linha.split(",")[4].trim())
                .collect(Collectors.groupingBy(jogador -> jogador, Collectors.counting()));

        Long maxCartoesVermelhos = contagemPorJogador.values().stream().max(Long::compare).orElse(0L);

        return contagemPorJogador.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxCartoesVermelhos))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private static List<String> partidasComMaisGols() {
        Stream<String> partidas = lerCSVAsStream("src/resources/campeonato-brasileiro-full.csv");

        Map<Integer, List<String>> partidasPorContagem = partidas
                .skip(1)
                .map(linha -> linha.split(","))
                .collect(Collectors.groupingBy(colunas -> {
                    int mandantePlacar = Integer.parseInt(colunas[12].trim().replace("\"", ""));
                    int visitantePlacar = Integer.parseInt(colunas[13].trim().replace("\"", ""));
                    return mandantePlacar + visitantePlacar;
                }, Collectors.mapping(colunas -> colunas[4].concat(colunas[12]) + " X " + colunas[5].concat(colunas[13]), Collectors.toList())));


        int maxGols = Collections.max(partidasPorContagem.keySet());

        List<String> partidasComMaxGols = partidasPorContagem.get(maxGols);

        return partidasComMaxGols;
    }





    public static void main(String[] args) {
        // Adir Silva Filho:
        //O time que mais venceu jogos no ano 2008
        System.out.println("\nTime(s) com maior número de vitórias:");
        //timeComMaisVitorias().forEach(System.out::println);

        //O Estado que teve menos jogos dentro do período 2003 e 2022
        System.out.println("\nEstado(s) com menor número de partidas:");
        //estadoComMenosVitoria().forEach(System.out::println);
        System.out.println();

        //O jogador que mais fez gols
        //O jogador que mais fez gols de pênaltis
        jogadorComMaisGols().forEach(System.out::println);
        jogadorComMaisGolsDePenalti().forEach(System.out::println);

        //O jogador que mais fez gols contras
        //O jogador que mais recebeu cartões amarelos

        //Matheus Vitor
        //O jogador que mais recebeu cartões vermelhos
        //O placar da partida com mais gols.
        System.out.println("Jogador(es) com mais cartões vermelhos:");
        jogadorComMaisCartoesVermelhos().forEach(System.out::println);
        System.out.println("Partida(s) com mais gols:");
        partidasComMaisGols().forEach(System.out::println);

    }
}
