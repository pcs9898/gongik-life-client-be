package org.example.gongiklifeclientbeinstitutionservice.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CsvUtil {

  public List<String[]> readCsv(String fileName) throws IOException, CsvException {
    try (CSVReader reader = new CSVReader(
        new InputStreamReader(getClass().getResourceAsStream("/" + fileName)))) {
      return reader.readAll();
    }
  }
}
