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

@Configuration
public class PolicyBatchConfiguration {


  @Bean(name = "importPolicyJob")
  public Job importPolicyJob(JobRepository jobRepository, Step policyStep1,
      PolicyJobCompletionNotificationListener listener) {
    return new JobBuilder("importPolicyJob", jobRepository)
        .listener(listener)
        .start(policyStep1)
        .build();
  }

  @Bean
  public Step policyStep1(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      FlatFileItemReader<Policy> policyFlatFileItemReader,
      JdbcBatchItemWriter<Policy> policyJdbcBatchItemWriter) {
    return new StepBuilder("policyStep1", jobRepository)
        .<Policy, Policy>chunk(10, transactionManager)
        .reader(policyFlatFileItemReader)
        .writer(policyJdbcBatchItemWriter)
        .build();
  }

  @Bean
  public FlatFileItemReader<Policy> policyFlatFileItemReader() {
    FlatFileItemReader<Policy> reader = new FlatFileItemReader<>();
    reader.setResource(new ClassPathResource("/input/CUSTCOMP01.txt"));
    reader.setName("policyItemReader");

    DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
    tokenizer.setDelimiter("|");
    tokenizer.setNames(
        "Chdrnum", "Cownnum", "OwnerName", "LifcNum", "LifcName", "Aracde", "Agntnum",
        "MailAddress");
    tokenizer.setIncludedFields(0, 1, 2, 3, 4, 5, 6, 7);

    BeanWrapperFieldSetMapper<Policy> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(Policy.class);

    DefaultLineMapper<Policy> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(tokenizer);
    lineMapper.setFieldSetMapper(fieldSetMapper);

    reader.setLineMapper(lineMapper);

    return reader;
  }


  @Bean
  public JdbcBatchItemWriter<Policy> policyJdbcBatchItemWriter(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Policy>()
        .sql("INSERT INTO policy (" +
            "chdrnum, " +
            "cownnum, " +
            "owner_name, " +
            "lifc_num, " +
            "lifc_name, " +
            "aracde, " +
            "agntnum, " +
            "mail_address) " +
            "VALUES (:chdrnum, :cownnum, :ownerName, :lifcNum, :lifcName, :aracde, :agntnum, :mailAddress)")
        .dataSource(dataSource)
        .beanMapped()
        .build();
  }

}
