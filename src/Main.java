import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static List<String> jogadorComMaisGolsContra() {
        return jogadorComMaisGolsFiltro(linha -> linha.contains("Gol Contra"));
    }


    private static List<String> jogadorComMaisCartaoFiltro(Predicate<String> filtro) {
        Stream<String> cartao = lerCSVAsStream("src/resources/campeonato-brasileiro-cartoes.csv");


        Map<String, Long> contagemCartaoPorJogador = cartao
                .filter(filtro)
                .map(linha -> linha.split(",")[4].trim())
                .collect(Collectors.groupingBy(jogador -> jogador, Collectors.counting()));

        Long maxCartao = contagemCartaoPorJogador.values().stream().max(Long::compare).orElse(0L);


        return contagemCartaoPorJogador.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxCartao))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    public static List<String> jogadorComMaisCartaoAmarelo() {
        return jogadorComMaisCartaoFiltro(linha -> linha.contains("Amarelo"));
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

    public static List<String> estadoComMenosVitoria() {
        return estatisticasCampeonato(
                linha -> !linha.contains("mandante"),
                14,
                "min"
        );
    }

    public static void main(String[] args) {
        // Adir Silva Filho:
        //O time que mais venceu jogos no ano 2008
        System.out.println("\nTime(s) com maior número de vitórias no ano 2008:");
        timeComMaisVitorias().forEach(System.out::println);
        //O Estado que teve menos jogos dentro do período 2003 e 2022
        System.out.println("\nEstado(s) com menor número de partidas do período 2003 e 2022:");
        estadoComMenosVitoria().forEach(System.out::println);
        System.out.println();

        System.out.print("Estatistica de Gols");
        //O jogador que mais fez gols
        System.out.println("\nO jogador(es) que mais fez gols:");
        jogadorComMaisGols().forEach(System.out::println);
        //O jogador que mais fez gols de pênaltis
        System.out.println("\nO jogador(es) que mais fez gols de pênalti:");
        jogadorComMaisGolsDePenalti().forEach(System.out::println);
        //O jogador que mais fez gols contras
        System.out.println("\nJogador(es) com mais gols contra:");
        jogadorComMaisGolsContra().forEach(System.out::println);
        System.out.println();

        System.out.print("Estatistica de Cartões");
        //O jogador que mais recebeu cartões amarelos
        System.out.println("\nJogador(es) com mais cartões Amarelo:");
        jogadorComMaisCartaoAmarelo().forEach(System.out::println);
        //O jogador que mais recebeu cartões vermelhos
        System.out.println("\nJogador(es) com mais cartões Vermelho:");
        System.out.println();

        System.out.print("Placar da partida");
        //O placar da partida com mais gols.

    }
}
