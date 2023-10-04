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

    private static List<AbstractMap.SimpleEntry<String, Long>> jogadorComMaisGolsFiltro(Predicate<String> filtro) {
        Stream<String> gols = lerCSVAsStream("src/resources/campeonato-brasileiro-gols.csv");

        Map<String, Long> contagemPorJogador = gols
                .filter(filtro)
                .map(linha -> linha.split(",")[3].trim())
                .collect(Collectors.groupingBy(jogador -> jogador, Collectors.counting()));

        Long maxGols = contagemPorJogador.values().stream().max(Long::compare).orElse(0L);

        return contagemPorJogador.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxGols))
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public static List<AbstractMap.SimpleEntry<String, Long>> jogadorComMaisGols() {
        return jogadorComMaisGolsFiltro(linha -> true);
    }

    public static List<AbstractMap.SimpleEntry<String, Long>> jogadorComMaisGolsDePenalti() {
        return jogadorComMaisGolsFiltro(linha -> linha.contains("Penalty"));
    }

    public static List<AbstractMap.SimpleEntry<String, Long>> jogadorComMaisGolsContra() {
        return jogadorComMaisGolsFiltro(linha -> linha.contains("Gol Contra"));
    }

    private static List<AbstractMap.SimpleEntry<String, Long>> jogadorComMaisCartaoFiltro(Predicate<String> filtro) {
        Stream<String> cartao = lerCSVAsStream("src/resources/campeonato-brasileiro-cartoes.csv");

        Map<String, Long> contagemCartaoPorJogador = cartao
                .filter(filtro)
                .map(linha -> linha.split(",")[4].trim())
                .collect(Collectors.groupingBy(jogador -> jogador, Collectors.counting()));

        Long maxCartao = contagemCartaoPorJogador.values().stream().max(Long::compare).orElse(0L);

        return contagemCartaoPorJogador.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxCartao))
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
  
    public static List<AbstractMap.SimpleEntry<String, Long>> jogadorComMaisCartaoAmarelo() {
        return jogadorComMaisCartaoFiltro(linha -> linha.contains("Amarelo"));
    }

    private static List<AbstractMap.SimpleEntry<String, Long>> jogadorComMaisCartoesVermelhos() {
        return jogadorComMaisCartaoFiltro(linha -> linha.contains("Vermelho"));
    }

    public static List<String> estatisticasCampeonato(
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

    public static List<String> estadoQueMenosRecebeuJogos() {
        return estatisticasCampeonato(
                linha -> !linha.contains("mandante"),
                14,
                "min"
        );
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
        System.out.println("=============================================");
        System.out.println("                 ESTATÍSTICAS              ");
        System.out.println("=============================================");

        System.out.println("\n▶ Time(s) com maior número de vitórias no ano 2008:");
        timeComMaisVitorias().forEach(time -> System.out.println("   • " + time));

        System.out.println("\n▶ Estado(s) com menor número de partidas do período 2003 e 2022:");
        estadoQueMenosRecebeuJogos().forEach(estado -> System.out.println("   • " + estado));
        System.out.println("\n---------------------------------------------");

        System.out.println("\n▶ Estatística de Gols");
        System.out.println("\n   • Jogador(es) que mais fizeram gols:");
        jogadorComMaisGols().forEach(entry -> System.out.println("     - " + entry.getKey() + ": " + entry.getValue() + " gols"));

        System.out.println("\n   • Jogador(es) que mais fizeram gols de pênalti:");
        jogadorComMaisGolsDePenalti().forEach(entry -> System.out.println("     - " + entry.getKey() + ": " + entry.getValue() + " gols"));

        System.out.println("\n   • Jogador(es) com mais gols contra:");
        jogadorComMaisGolsContra().forEach(entry -> System.out.println("     - " + entry.getKey() + ": " + entry.getValue() + " gols"));

        System.out.println("\n▶ Estatística de Cartões");
        System.out.println("\n   • Jogador(es) com mais cartões amarelos:");
        jogadorComMaisCartaoAmarelo().forEach(entry -> System.out.println("     - " + entry.getKey() + ": " + entry.getValue() + " cartões"));

        System.out.println("\n   • Jogador(es) com mais cartões vermelhos:");
        jogadorComMaisCartoesVermelhos().forEach(entry -> System.out.println("     - " + entry.getKey() + ": " + entry.getValue() + " cartões"));

        System.out.println("\n▶ Estatística de Partidas");
        System.out.println("\n   • Partida(s) com mais gols:");
        partidasComMaisGols().forEach(partida -> System.out.println("     - " + partida));

        System.out.println("\n=============================================");
        System.out.println("              FIM DE RELATÓRIO             ");
        System.out.println("=============================================");

    }
}
