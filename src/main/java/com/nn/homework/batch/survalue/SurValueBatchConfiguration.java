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


  @Bean(name = "importSurValueJob")
  public Job importSurValueJob(JobRepository jobRepository, Step surValueStep1,
      SurValueJobCompletionNotificationListener listener) {
    return new JobBuilder("importSurValueJob", jobRepository)
        .listener(listener)
        .start(surValueStep1)
        .build();
  }

  @Bean
  public Step surValueStep1(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      FlatFileItemReader<SurValue> surValueFlatFileItemReader,
      JdbcBatchItemWriter<SurValue> surValueJdbcBatchItemWriter) {
    return new StepBuilder("surValueStep1", jobRepository)
        .<SurValue, SurValue>chunk(10, transactionManager)
        .reader(surValueFlatFileItemReader)
        .writer(surValueJdbcBatchItemWriter)
        .build();
  }

  @Bean
  public FlatFileItemReader<SurValue> surValueFlatFileItemReader() {
    FlatFileItemReader<SurValue> reader = new FlatFileItemReader<>();
    reader.setResource(new ClassPathResource("/input/ZTPSPF.txt"));
    reader.setName("SurValueItemReader");

    FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    tokenizer.setNames("Company", "Chdrnum", "SurrenderValue");
    tokenizer.setColumns(new Range(1, 1), new Range(2, 9), new Range(10, 24));
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
        .sql("INSERT INTO sur_value (" +
            "chdrnum, " +
            "surrender_value, " +
            "company) " +
            "VALUES (:Chdrnum, :SurrenderValue, :Company)")
        .dataSource(dataSource)
        .beanMapped()
        .build();
  }
}