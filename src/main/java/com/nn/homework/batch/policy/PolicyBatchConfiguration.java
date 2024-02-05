package com.nn.homework.batch.policy;

import com.nn.homework.domain.Policy;
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

/**
 * Configuration class for batch processing of Policy entities.
 *
 * <p>This class sets up a Spring Batch job that reads Policy data from a delimited text file and
 * writes it to a database. It includes configuration for the job and its steps, item reader and
 * writer components, and other necessary Spring Batch infrastructure.</p>
 */
@Configuration
public class PolicyBatchConfiguration {

  private static final String JOB_NAME = "importPolicyJob";
  private static final String STEP_NAME = "policyStep1";
  private static final String READER_NAME = "policyItemReader";
  private static final String FILE_PATH = "/input/CUSTCOMP01.txt";
  private static final String FILE_ENCODING = "WINDOWS-1252";
  private static final String DELIMITER = "|";
  private static final String[] FIELD_NAMES = new String[]{
      "Chdrnum", "Cownnum", "OwnerName", "LifcNum", "LifcName", "Aracde", "Agntnum", "MailAddress"
  };
  private static final int[] INCLUDED_FIELDS = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
  private static final String INSERT_SQL = "INSERT INTO policy (" +
      "chdrnum, cownnum, owner_name, lifc_num, lifc_name, aracde, agntnum, mail_address) " +
      "VALUES (:chdrnum, :cownnum, :ownerName, :lifcNum, :lifcName, :aracde, :agntnum, :mailAddress)";

  /**
   * Configures the job responsible for importing Policy data.
   *
   * @param jobRepository JobRepository for job persistence.
   * @param policyStep1   Step for processing Policy data.
   * @param listener      Listener for job completion notifications.
   * @return Configured Job instance.
   */
  @Bean
  public Job importPolicyJob(JobRepository jobRepository, Step policyStep1,
      PolicyJobCompletionNotificationListener listener) {
    return new JobBuilder(JOB_NAME, jobRepository)
        .listener(listener)
        .start(policyStep1)
        .build();
  }

  /**
   * Configures the step for processing Policy data.
   *
   * @param jobRepository             JobRepository for job persistence.
   * @param transactionManager        PlatformTransactionManager for transaction management.
   * @param policyFlatFileItemReader  Reader for Policy data from a flat file.
   * @param policyJdbcBatchItemWriter Writer for Policy data to the database.
   * @return Configured Step instance.
   */
  @Bean
  public Step policyStep1(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      FlatFileItemReader<Policy> policyFlatFileItemReader,
      JdbcBatchItemWriter<Policy> policyJdbcBatchItemWriter) {
    return new StepBuilder(STEP_NAME, jobRepository)
        .<Policy, Policy>chunk(10, transactionManager)
        .reader(policyFlatFileItemReader)
        .writer(policyJdbcBatchItemWriter)
        .build();
  }

  /**
   * Configures the FlatFileItemReader for reading Policy data from a flat file.
   *
   * @return Configured FlatFileItemReader instance.
   */
  @Bean
  public FlatFileItemReader<Policy> policyFlatFileItemReader() {
    FlatFileItemReader<Policy> reader = new FlatFileItemReader<>();
    reader.setResource(new ClassPathResource(FILE_PATH));
    reader.setName(READER_NAME);
    reader.setEncoding(FILE_ENCODING);

    DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
    tokenizer.setDelimiter(DELIMITER);
    tokenizer.setNames(FIELD_NAMES);
    tokenizer.setIncludedFields(INCLUDED_FIELDS);

    BeanWrapperFieldSetMapper<Policy> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(Policy.class);

    DefaultLineMapper<Policy> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(tokenizer);
    lineMapper.setFieldSetMapper(fieldSetMapper);

    reader.setLineMapper(lineMapper);

    return reader;
  }

  /**
   * Configures the JdbcBatchItemWriter for writing Policy data to the database.
   *
   * @param dataSource DataSource for database connections.
   * @return Configured JdbcBatchItemWriter instance.
   */
  @Bean
  public JdbcBatchItemWriter<Policy> policyJdbcBatchItemWriter(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Policy>()
        .sql(INSERT_SQL)
        .dataSource(dataSource)
        .beanMapped()
        .build();
  }
}