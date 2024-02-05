package com.nn.homework.batch.outpayheader;


import com.nn.homework.domain.OutPayHeader;
import java.beans.PropertyEditor;
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


  @Bean(name = "importOutPayHeaderJob")
  public Job importOutPayHeaderJob(JobRepository jobRepository, Step outPayHeaderStep1,
      OutPayHeaderJobCompletionNotificationListener listener) {
    return new JobBuilder("importOutPayHeaderJob", jobRepository)
        .listener(listener)
        .start(outPayHeaderStep1)
        .build();
  }

  @Bean
  public Step outPayHeaderStep1(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      FlatFileItemReader<OutPayHeader> outPayHeaderFlatFileItemReader,
      JdbcBatchItemWriter<OutPayHeader> outPayHeaderJdbcBatchItemWriter) {
    return new StepBuilder("outPayHeaderStep1", jobRepository)
        .<OutPayHeader, OutPayHeader>chunk(10, transactionManager)
        .reader(outPayHeaderFlatFileItemReader)
        .writer(outPayHeaderJdbcBatchItemWriter)
        .build();
  }

  @Bean
  public FlatFileItemReader<OutPayHeader> outPayHeaderFlatFileItemReader() {
    FlatFileItemReader<OutPayHeader> reader = new FlatFileItemReader<>();
    reader.setResource(new ClassPathResource("/input/OUTPH_CUP_20200204_1829.TXT"));
    reader.setName("outPayHeaderItemReader");
    reader.setEncoding("UTF-8"); // Adjust if necessary

    DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
    tokenizer.setDelimiter(";");
    tokenizer.setNames("clntnum", "chdrnum", "letterType", "printDate", "dataID", "clntName",
        "clntAddress",
        "role1", "cownNum", "cownName");
    tokenizer.setIncludedFields(0, 1, 2, 3, 4, 5, 6, 9, 11, 12);

    BeanWrapperFieldSetMapper<OutPayHeader> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(OutPayHeader.class);

    Map<Class<?>, PropertyEditor> customEditors = new HashMap<>();
    customEditors.put(LocalDate.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        try {
          setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyyMMdd")));
        } catch (DateTimeParseException e) {
          setValue(null);
        }
      }
    });

    customEditors.put(BigDecimal.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        try {
          setValue(new BigDecimal(text));
        } catch (NumberFormatException e) {
          setValue(BigDecimal.ZERO);
        }
      }
    });

    fieldSetMapper.setCustomEditors(customEditors);

    reader.setLineMapper(new DefaultLineMapper<OutPayHeader>() {{
      setLineTokenizer(tokenizer);
      setFieldSetMapper(fieldSetMapper);
    }});

    return reader;
  }

  @Bean
  public JdbcBatchItemWriter<OutPayHeader> outPayHeaderJdbcBatchItemWriter(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<OutPayHeader>()
        .sql("INSERT INTO out_pay_header (" +
            "clntnum, chdrnum, letter_type, print_date, dataID, " +
            "clnt_name, clnt_address, role1, cown_num, cown_name) " +
            "VALUES (:clntnum, :chdrnum, :letterType, :printDate, :dataID, " +
            ":clntName, :clntAddress, :role1, :cownNum, :cownName)")
        .dataSource(dataSource)
        .beanMapped()
        .build();
  }

}
