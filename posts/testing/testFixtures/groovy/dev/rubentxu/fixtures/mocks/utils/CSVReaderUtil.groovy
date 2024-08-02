package dev.rubentxu.fixtures.mocks.utils

import com.opencsv.CSVReader

public class CSVReaderUtil {

    public static List<Map> readCSV(String filePath) {
        CSVReader reader = null
        try {
            reader = new CSVReader(new FileReader(filePath))
            List<String[]> lines = reader.readAll()
            if (lines.isEmpty()) {
                return Collections.emptyList()
            }

            String[] headers = lines.get(0)
            List<Map> result = new ArrayList<>()
            for (int i = 1; i < lines.size(); i++) {
                String[] values = lines.get(i)
                Map<String, Object> row = new HashMap<>()
                for (int j = 0; j < headers.length; j++) {
                    row.put(headers[j], values[j])
                }
                result.add(row)
            }
             return result
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo CSV: " + filePath, e)
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (IOException e) {
                    // Log the exception
                }
            }
        }
    }
}