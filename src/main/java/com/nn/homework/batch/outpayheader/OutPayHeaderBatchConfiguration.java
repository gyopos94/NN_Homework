package com.nn.homework.batch.outpayheader;

import com.nn.homework.domain.OutPayHeader;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class OutPayHeaderBatchConfiguration {

  private static final String JOB_NAME = "importOutPayHeaderJob";
  private static final String STEP_NAME = "outPayHeaderStep1";
  private static final String READER_NAME = "outPayHeaderItemReader";
  private static final String FILE_PATH = "/input/OUTPH_CUP_20200204_1829.TXT";
  private static final String FILE_ENCODING = "WINDOWS-1252";
  private static final String DELIMITER = ";";
  private static final String[] FIELD_NAMES = new String[]{
      "clntnum", "chdrnum", "letterType", "printDate", "dataID", "clntName", "clntAddress",
      "role1", "cownNum", "cownName"
  };
  private static final int[] INCLUDED_FIELDS = new int[]{0, 1, 2, 3, 4, 5, 6, 9, 11, 12};
  private static final String INSERT_SQL = "INSERT INTO out_pay_header (" +
      "clntnum, chdrnum, letter_type, print_date, dataID, " +
      "clnt_name, clnt_address, role1, cown_num, cown_name) " +
      "VALUES (:clntnum, :chdrnum, :letterType, :printDate, :dataID, " +
      ":clntName, :clntAddress, :role1, :cownNum, :cownName)";

  @Bean
  public Job importOutPayHeaderJob(JobRepository jobRepository, Step outPayHeaderStep1,
      OutPayHeaderJobCompletionNotificationListener listener) {
    return new JobBuilder(JOB_NAME, jobRepository)
        .listener(listener)
        .start(outPayHeaderStep1)
        .build();
  }

  @Bean
  public Step outPayHeaderStep1(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      FlatFileItemReader<OutPayHeader> outPayHeaderFlatFileItemReader,
      JdbcBatchItemWriter<OutPayHeader> outPayHeaderJdbcBatchItemWriter) {
    return new StepBuilder(STEP_NAME, jobRepository)
        .<OutPayHeader, OutPayHeader>chunk(10, transactionManager)
        .reader(outPayHeaderFlatFileItemReader)
        .writer(outPayHeaderJdbcBatchItemWriter)
        .build();
  }

  @Bean
  public FlatFileItemReader<OutPayHeader> outPayHeaderFlatFileItemReader() {
    FlatFileItemReader<OutPayHeader> reader = new FlatFileItemReader<>();
    reader.setResource(new ClassPathResource(FILE_PATH));
    reader.setName(READER_NAME);
    reader.setEncoding(FILE_ENCODING);

    DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
    tokenizer.setDelimiter(DELIMITER);
    tokenizer.setNames(FIELD_NAMES);
    tokenizer.setIncludedFields(INCLUDED_FIELDS);

    BeanWrapperFieldSetMapper<OutPayHeader> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(OutPayHeader.class);
    fieldSetMapper.setCustomEditors(getCustomEditors());

    DefaultLineMapper<OutPayHeader> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(tokenizer);
    lineMapper.setFieldSetMapper(fieldSetMapper);

    reader.setLineMapper(lineMapper);

    return reader;
  }

  private Map<Class<?>, PropertyEditorSupport> getCustomEditors() {
    Map<Class<?>, PropertyEditorSupport> customEditors = new HashMap<>();
    customEditors.put(LocalDate.class, new LocalDateEditor());
    customEditors.put(BigDecimal.class, new BigDecimalEditor());
    return customEditors;
  }

  public static class LocalDateEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
      try {
        setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyyMMdd")));
      } catch (DateTimeParseException e) {
        setValue(null);
      }
    }
  }

  public static class BigDecimalEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
      try {
        setValue(new BigDecimal(text));
      } catch (NumberFormatException e) {
        setValue(BigDecimal.ZERO);
      }
    }
  }

  @Bean
  public JdbcBatchItemWriter<OutPayHeader> outPayHeaderJdbcBatchItemWriter(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<OutPayHeader>()
        .sql(INSERT_SQL)
        .dataSource(dataSource)
        .beanMapped()
        .build();
  }
}