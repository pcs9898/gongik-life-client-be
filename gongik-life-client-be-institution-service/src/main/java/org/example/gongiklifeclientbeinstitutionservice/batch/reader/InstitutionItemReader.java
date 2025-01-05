package org.example.gongiklifeclientbeinstitutionservice.batch.reader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class InstitutionItemReader implements ItemReader<String[]> {

  private List<String[]> rows;
  private int nextRowIndex;

  @Value("classpath:csv-init-data/Institution_list.csv")
  private Resource resource;

  @PostConstruct
  public void initialize() throws IOException, CsvException {
    try (CSVReader csvReader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
      this.rows = csvReader.readAll();
      this.nextRowIndex = 0;
    } catch (IOException | CsvException e) {
      throw new IllegalStateException("Failed to read CSV file", e);
    }
  }

  @Override
  public String[] read() {
    if (nextRowIndex < rows.size()) {
      return rows.get(nextRowIndex++);
    }
    return null;
  }
}
