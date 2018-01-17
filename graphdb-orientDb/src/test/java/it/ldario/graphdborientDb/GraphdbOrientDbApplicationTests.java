package it.ldario.graphdborientDb;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrientDbConfiguration.class)

public class GraphdbOrientDbApplicationTests {

	@MockBean
	private OrientGraph orientGraph;
	@Test
	public void contextLoads() {
	}


}
