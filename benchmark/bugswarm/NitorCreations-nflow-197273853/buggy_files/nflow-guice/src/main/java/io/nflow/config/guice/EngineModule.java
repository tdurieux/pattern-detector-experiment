package io.nflow.config.guice;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ThreadFactory;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.h2.tools.Server;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.AbstractResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.util.Providers;

import io.nflow.engine.internal.config.EngineConfiguration;
import io.nflow.engine.internal.config.NFlow;
import io.nflow.engine.internal.executor.WorkflowInstanceExecutor;
import io.nflow.engine.internal.storage.db.DatabaseConfiguration;
import io.nflow.engine.internal.storage.db.DatabaseInitializer;
import io.nflow.engine.internal.storage.db.H2DatabaseConfiguration;
import io.nflow.engine.internal.storage.db.MysqlDatabaseConfiguration;
import io.nflow.engine.internal.storage.db.OracleDatabaseConfiguration;
import io.nflow.engine.internal.storage.db.PgDatabaseConfiguration;
import io.nflow.engine.internal.storage.db.SQLVariants;
import io.nflow.engine.service.WorkflowDefinitionService;

public class EngineModule extends AbstractModule {

  private final Object metricRegistry;
  private final Environment env;

  public EngineModule(final Properties properties, final Object metricRegistry) {
    this.env = new StandardEnvironment() {
      @Override
      protected void customizePropertySources(MutablePropertySources propertySources) {
        if (properties != null) {
          propertySources.addLast(new PropertiesPropertySource("nflowEngineProperties", properties));
        }
      }
    };
    this.metricRegistry = metricRegistry;
  }

  @Override
  protected void configure() {
    bind(Environment.class).toInstance(env);
    EngineConfiguration engineConfiguration = new EngineConfiguration();
    bind(AbstractResource.class).annotatedWith(NFlow.class)
        .toProvider(Providers.of(engineConfiguration.nflowNonSpringWorkflowsListing(env)));

    ThreadFactory factory = engineConfiguration.nflowThreadFactory();
    bind(ThreadFactory.class).annotatedWith(NFlow.class).toInstance(factory);
    bind(WorkflowInstanceExecutor.class).toInstance(engineConfiguration.nflowExecutor(factory, env));
    bind(ObjectMapper.class).annotatedWith(NFlow.class).toInstance(engineConfiguration.nflowObjectMapper());
    bindDatabase();
    requestInjection(this);
  }

  private void bindDatabase() {
    TransactionSynchronizationManager.initSynchronization();
    TransactionSynchronizationManager.setActualTransactionActive(true);

    DatabaseConfiguration db;

    String dbtype = env.getProperty("nflow.db.type", String.class);

    switch (dbtype) {
    case "h2":
      String tcpPort = env.getProperty("nflow.db.h2.tcp.port");
      if (!isBlank(tcpPort)) {
        try {
          Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", tcpPort).start();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
      String consolePort = env.getProperty("nflow.db.h2.console.port");
      if (!isBlank(consolePort)) {
        try {
          Server.createTcpServer("-webPort", consolePort).start();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
      db = new H2DatabaseConfiguration();
      break;
    case "mysql":
      db = new MysqlDatabaseConfiguration();
      break;
    case "oracle":
      db = new OracleDatabaseConfiguration();
      break;
    case "postgres":
      db = new PgDatabaseConfiguration();
      break;
    default:
      throw new RuntimeException("Unknown DB");
    }
    DataSource dataSource = db.nflowDatasource(env, metricRegistry);
    bind(DataSource.class).annotatedWith(NFlow.class).toInstance(dataSource);
    bind(DatabaseInitializer.class).annotatedWith(NFlow.class).toInstance(db.nflowDatabaseInitializer(dataSource, env));
    bind(JdbcTemplate.class).annotatedWith(NFlow.class).toInstance(db.nflowJdbcTemplate(dataSource));
    bind(NamedParameterJdbcTemplate.class).annotatedWith(NFlow.class).toInstance(db.nflowNamedParameterJdbcTemplate(dataSource));
    bind(TransactionTemplate.class).annotatedWith(NFlow.class)
        .toInstance(db.nflowTransactionTemplate(new DataSourceTransactionManager(dataSource)));
    bind(SQLVariants.class).toInstance(db.sqlVariants());
    bind(PlatformTransactionManager.class).toInstance(new DataSourceTransactionManager(dataSource));
  }

  @Inject
  void initPostConstruct(WorkflowDefinitionService workflowDefinitionService) throws Exception {
    workflowDefinitionService.postProcessWorkflowDefinitions();
  }
}
