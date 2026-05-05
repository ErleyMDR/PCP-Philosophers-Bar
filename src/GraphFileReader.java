package PhilosophersBar.src;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class GraphFileReader {

    public int[][] readFile(String fileName) throws java.io.IOException {
        File file = getAndValidateFile(fileName);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String firstLine = br.readLine();
            if (firstLine.equalsIgnoreCase("BEGIN")) {
                List<String> adjMatLines = br.lines()
                        .filter(line -> !line.startsWith("BEGIN") || !line.startsWith("END")).toList();
                int[][] adjMat = new int[adjMatLines.size()][];
                for (int i = 0; i < adjMatLines.size(); i++) {
                    String[] colArr = adjMatLines.get(i).trim().split(", ");
                    adjMat[i] = Arrays.stream(colArr).mapToInt(Integer::valueOf).toArray();
                }

                return adjMat;
            } else throw new IOException("Graph file formating is invalid");
        }
    }

    private File getAndValidateFile(String fileName) throws FileNotFoundException{
        String filePath = GraphFileReader.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        String predicate = File.separator + "PhilosophersBar" + File.separator;
        String graphFilePath;
        if (filePath.contains(predicate + "src")) {
            graphFilePath = filePath.substring(0, filePath.indexOf("src"));
            graphFilePath = graphFilePath + "testCases" + File.separator + fileName;
        } else throw new FileNotFoundException();

        File file = new File(graphFilePath);
        if (!file.exists()) {
            System.out.printf("File %s does not exist\n", graphFilePath);
            throw new FileNotFoundException();
        }
        return file;
    }
}
