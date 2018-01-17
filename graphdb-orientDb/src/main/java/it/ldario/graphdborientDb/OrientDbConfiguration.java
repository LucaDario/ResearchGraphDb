package it.ldario.graphdborientDb;

import com.orientechnologies.orient.core.exception.OStorageException;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.frames.FramedGraphFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

@Configuration
@PropertySource("classpath:application.properties")
public class OrientDbConfiguration {

    @Value("${spring.data.orientdb.username}")
    private String username;
    @Value("${spring.data.orientdb.password}")
    private String password;
    @Value("${spring.data.orientdb.url}")
    private String fullDatabaseName;

    @Bean
    OrientGraph factory() throws IOException {
        try {
            OrientGraphFactory factory = new OrientGraphFactory(fullDatabaseName, username, password);
            FramedGraphFactory framedFactory = new FramedGraphFactory();
            OrientGraph instance = factory.getTx();
            //FramedTransactionalGraph<OrientGraph> framedGraph = framedFactory.create(instance);
            return instance;
        }
        catch (OStorageException e){
            System.out.println("Not found OrientDb in "+fullDatabaseName);
           throw e;
        }
    }
}
