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

/**
 * Configuration class for batch processing of SurValue entities.
 *
 * <p>This class sets up a Spring Batch job designed to read SurValue data from a fixed-length
 * formatted text file and write it into a database. It configures the necessary components for the
 * job, including item readers and writers, and the steps involved in the data processing.</p>
 */
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

  /**
   * Configures the job responsible for importing SurValue data.
   *
   * @param jobRepository JobRepository used for job persistence.
   * @param surValueStep1 Step that defines the bulk of the SurValue import process.
   * @param listener      Listener that acts upon completion of the SurValue import job.
   * @return A fully configured Job instance for importing SurValue data.
   */
  @Bean
  public Job importSurValueJob(JobRepository jobRepository, Step surValueStep1,
      SurValueJobCompletionNotificationListener listener) {
    return new JobBuilder(JOB_NAME, jobRepository)
        .listener(listener)
        .start(surValueStep1)
        .build();
  }

  /**
   * Defines the step for processing SurValue data, including reading from the file and writing to
   * the database.
   *
   * @param jobRepository               JobRepository used for job persistence.
   * @param transactionManager          PlatformTransactionManager used for transaction management
   *                                    within the step.
   * @param surValueFlatFileItemReader  Reader configured to read SurValue data from a flat file.
   * @param surValueJdbcBatchItemWriter Writer configured to write SurValue data into the database.
   * @return A Step configured to process SurValue data.
   */
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

  /**
   * Configures the FlatFileItemReader for reading SurValue data from a fixed-length formatted
   * file.
   *
   * @return A FlatFileItemReader instance configured to read SurValue data.
   */
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

  /**
   * Configures the JdbcBatchItemWriter for writing SurValue data into the database.
   *
   * @param dataSource DataSource providing the connection to the database.
   * @return A JdbcBatchItemWriter instance configured for SurValue data.
   */
  @Bean
  public JdbcBatchItemWriter<SurValue> surValueJdbcBatchItemWriter(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<SurValue>()
        .sql(INSERT_SQL)
        .dataSource(dataSource)
        .beanMapped()
        .build();
  }
}