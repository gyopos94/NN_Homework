package com.nn.homework.batch.survalue;

import com.nn.homework.domain.SurValue;
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
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SurValueBatchConfiguration {

  private static final String JOB_NAME = "importSurValueJob";
  private static final String STEP_NAME = "surValueStep1";
  private static final String READER_NAME = "SurValueItemReader";
  private static final String FILE_PATH = "/input/ZTPSPF.txt";
  private static final String FILE_ENCODING = "WINDOWS-1252";
  private static final String INSERT_SQL = "INSERT INTO sur_value (chdrnum, surrender_value, company) VALUES (:Chdrnum, :SurrenderValue, :Company)";
  private static final String[] NAMES = {"Company", "Chdrnum", "SurrenderValue"};
  private static final Range[] RANGES = {new Range(1, 1), new Range(2, 9), new Range(10, 24)};

  @Bean(name = JOB_NAME)
  public Job importSurValueJob(JobRepository jobRepository, Step surValueStep1,
      SurValueJobCompletionNotificationListener listener) {
    return new JobBuilder(JOB_NAME, jobRepository)
        .listener(listener)
        .start(surValueStep1)
        .build();
  }

  @Bean
  public Step surValueStep1(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      FlatFileItemReader<SurValue> surValueFlatFileItemReader,
      JdbcBatchItemWriter<SurValue> surValueJdbcBatchItemWriter) {
    return new StepBuilder(STEP_NAME, jobRepository)
        .<SurValue, SurValue>chunk(10, transactionManager)
        .reader(surValueFlatFileItemReader)
        .writer(surValueJdbcBatchItemWriter)
        .build();
  }

  @Bean
  public FlatFileItemReader<SurValue> surValueFlatFileItemReader() {
    FlatFileItemReader<SurValue> reader = new FlatFileItemReader<>();
    reader.setResource(new ClassPathResource(FILE_PATH));
    reader.setName(READER_NAME);
    reader.setEncoding(FILE_ENCODING);

    FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    tokenizer.setNames(NAMES);
    tokenizer.setColumns(RANGES);
    tokenizer.setStrict(false);

    BeanWrapperFieldSetMapper<SurValue> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(SurValue.class);

    DefaultLineMapper<SurValue> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(tokenizer);
    lineMapper.setFieldSetMapper(fieldSetMapper);

    reader.setLineMapper(lineMapper);

    return reader;
  }

  @Bean
  public JdbcBatchItemWriter<SurValue> surValueJdbcBatchItemWriter(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<SurValue>()
        .sql(INSERT_SQL)
        .dataSource(dataSource)
        .beanMapped()
        .build();
  }
}