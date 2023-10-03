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


    public static void main(String[] args) {
        //O time que mais venceu jogos no ano 2008
        //O Estado que teve menos jogos dentro do período 2003 e 2022

        //O jogador que mais fez gols
        //O jogador que mais fez gols de pênaltis
        jogadorComMaisGols().forEach(System.out::println);
        jogadorComMaisGolsDePenalti().forEach(System.out::println);

        //O jogador que mais fez gols contras
        //O jogador que mais recebeu cartões amarelos

        //O jogador que mais recebeu cartões vermelhos
        //O placar da partida com mais gols.

    }
}