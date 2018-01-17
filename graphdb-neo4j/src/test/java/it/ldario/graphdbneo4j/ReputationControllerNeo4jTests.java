package it.ldario.graphdbneo4j;


import it.ldario.basegraphdb.dbentity.TransactionBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest

public class ReputationControllerNeo4jTests {

    @MockBean
    private AccountIdNeo4jRepository accountIdNeo4jRepository;

    @MockBean
    private EntityIdNeo4jRepository entityIdNeo4jRepository;

    @MockBean
    private TransactionNeo4jRepository transactionNeo4jRepository;

    @Autowired
    private ReputationControllerNeo4j reputationControllerNeo4j;


    @Test
    public void addTransactionWhitPayeeAndPayerNotInDb() {


        when(entityIdNeo4jRepository.getEntityId(any(String.class))).thenReturn(() -> new Iterator<EntityIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public EntityIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });


        when(accountIdNeo4jRepository.getAccountId(any(String.class))).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public AccountIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });

        assertThat(reputationControllerNeo4j.addTransaction("Luca", "IT321", new TransactionBuilder().build())).isEqualTo(2);

    }

    @Test
    public void addTransactionWithPayeeNotInDbAndPayerIsAccountId() {

        when(entityIdNeo4jRepository.getEntityId(any(String.class))).thenReturn(() -> new Iterator<EntityIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public EntityIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });

        when(accountIdNeo4jRepository.getAccountId("IT123")).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public AccountIdNeo4j next() {
                return new AccountIdNeo4j("id");
            }
        });
        when(accountIdNeo4jRepository.getAccountId("IT321")).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public AccountIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });


        assertThat(reputationControllerNeo4j.addTransaction("IT123", "IT321", new TransactionBuilder().build())).isEqualTo(0);


    }

    @Test
    public void addTransactionWithPayeeNotInDbAndPayerIsEntityId() {

        when(entityIdNeo4jRepository.getEntityId("Luca")).thenReturn(() -> new Iterator<EntityIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public EntityIdNeo4j next() {
                return new EntityIdNeo4j("Luca");
            }
        });

        when(accountIdNeo4jRepository.getAccountId(any(String.class))).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public AccountIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });

        when(entityIdNeo4jRepository.getEntityId("Jacopo")).thenReturn(() -> new Iterator<EntityIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public EntityIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });

        assertThat(reputationControllerNeo4j.addTransaction("Luca", "IT321", new TransactionBuilder().build())).isEqualTo(0);


    }

    @Test
    public void addTransactionWithPayerNotInDbAndPayeeIsAccountId() {


        when(accountIdNeo4jRepository.getAccountId("IT321")).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public AccountIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });
        when(accountIdNeo4jRepository.getAccountId("IT123")).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public AccountIdNeo4j next() {
                return new AccountIdNeo4j("IT123");
            }
        });

        when(entityIdNeo4jRepository.getEntityId(any(String.class))).thenReturn(() -> new Iterator<EntityIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public EntityIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });
        assertThat(reputationControllerNeo4j.addTransaction("IT321", "IT123", new TransactionBuilder().build())).isEqualTo(1);

    }

    @Test
    public void addTransactionWithPayeeAndPayerIdDb() {
        when(accountIdNeo4jRepository.getAccountId("IT321")).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public AccountIdNeo4j next() {
                return new AccountIdNeo4j("IT321");
            }
        });
        when(entityIdNeo4jRepository.getEntityId("Luca")).thenReturn(() -> new Iterator<EntityIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public EntityIdNeo4j next() {
                return new EntityIdNeo4j("Luca");
            }
        });
        when(entityIdNeo4jRepository.getEntityId("IT321")).thenReturn(() -> new Iterator<EntityIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public EntityIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });
        when(accountIdNeo4jRepository.getAccountId("Luca")).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public AccountIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });


        assertThat(reputationControllerNeo4j.addTransaction("Luca", "IT321", new TransactionBuilder().build())).isEqualTo(3);


    }

    @Test
    public void getTotalReputationWithPayeeNotInDb() {
        when(accountIdNeo4jRepository.getTotalReputation("IT123")).thenReturn(50);
        when(accountIdNeo4jRepository.getAccountId("IT123")).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public AccountIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });
        assertThat(reputationControllerNeo4j.getTotalReputation("IT123")).isEqualTo(-1);

    }

    @Test
    public void getTotalReputationWithPayeeInDb() {
        when(accountIdNeo4jRepository.getTotalReputation("IT123")).thenReturn(50);
        when(accountIdNeo4jRepository.getAccountId("IT123")).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public AccountIdNeo4j next() {
                return new AccountIdNeo4j("IT123");
            }
        });
        assertThat(reputationControllerNeo4j.getTotalReputation("IT123")).isEqualTo(50);
    }

    @Test
    public void getRelativeReputationWithPayeeNotInDb() {

        when(accountIdNeo4jRepository.getTotalReputation("IT123")).thenReturn(50);
        when(entityIdNeo4jRepository.getEntityId(any(String.class))).thenReturn(() -> new Iterator<EntityIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public EntityIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });

        when(accountIdNeo4jRepository.getAccountId(any(String.class))).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public AccountIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });

        assertThat(reputationControllerNeo4j.getRelativeReputation("IT123", "Luca")).isEqualTo(-1);

    }

    @Test
    public void getRelativeReputationWithPayeeInDbAndPayerIsEntityId() {

        when(entityIdNeo4jRepository.getRelativeReputation("Luca", "IT123")).thenReturn(50);

        when(accountIdNeo4jRepository.getAccountId("IT123")).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public AccountIdNeo4j next() {
                return new AccountIdNeo4j("IT123");
            }
        });
        when(accountIdNeo4jRepository.getAccountId("Luca")).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public AccountIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });

        when(entityIdNeo4jRepository.getEntityId(any(String.class))).thenReturn(() -> new Iterator<EntityIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public EntityIdNeo4j next() {
                return new EntityIdNeo4j("Luca");
            }
        });
        assertThat(reputationControllerNeo4j.getRelativeReputation("IT123", "Luca")).isEqualTo(50);

    }

    @Test
    public void getRelatieReputationWithPayeeInDbAndPayerIsAccountId() {
        when(accountIdNeo4jRepository.getRelativeReputation("any", "any")).thenReturn(50);

        when(accountIdNeo4jRepository.getAccountId(any(String.class))).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public AccountIdNeo4j next() {
                return new AccountIdNeo4j("any");
            }
        });

        when(entityIdNeo4jRepository.getEntityId(any(String.class))).thenReturn(() -> new Iterator<EntityIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public EntityIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });

        assertThat(reputationControllerNeo4j.getRelativeReputation("any", "any")).isEqualTo(50);

    }

    @Test
    public void getGlobalReputationAndPayeeIsAccountId() {
        when(accountIdNeo4jRepository.getGlobalReputationForAccountId(any(String.class))).thenReturn(50);

        when(accountIdNeo4jRepository.getAccountId(any(String.class))).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public AccountIdNeo4j next() {
                return new AccountIdNeo4j("any");
            }
        });

        when(entityIdNeo4jRepository.getEntityId(any(String.class))).thenReturn(() -> new Iterator<EntityIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public EntityIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });

        assertThat(reputationControllerNeo4j.getGlobalReputation("any")).isEqualTo(50);


    }

    @Test
    public void getGlobalReputationAndPayeeIsEntity() {
        when(accountIdNeo4jRepository.getGlobalReputationForEntityId(any(String.class))).thenReturn(50);

        when(accountIdNeo4jRepository.getAccountId(any(String.class))).thenReturn(() -> new Iterator<AccountIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public AccountIdNeo4j next() {
                throw new NoSuchElementException();
            }
        });

        when(entityIdNeo4jRepository.getEntityId(any(String.class))).thenReturn(() -> new Iterator<EntityIdNeo4j>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public EntityIdNeo4j next() {
                return new EntityIdNeo4j("any");
            }
        });

        assertThat(reputationControllerNeo4j.getGlobalReputation("any")).isEqualTo(50);

    }


    @Test
    public void getAmountAccountIdIntheLastPeriod() {
        when(accountIdNeo4jRepository.getAmountAccountInLastPeriod(any(String.class), any(Long.class))).thenReturn(50);

        assertThat(reputationControllerNeo4j.getAmountAccountIdInATimeRange("Luca", 10)).isEqualTo(50);
    }

    @Test
    public void getAmountAccountIdWithoutLastPeriod() {
        when(accountIdNeo4jRepository.getAmountAccountWithoutLastPeriod(any(String.class), any(Long.class))).thenReturn(50);

        assertThat(reputationControllerNeo4j.getAmountAccountIdInATimeRange("Luca", -10)).isEqualTo(50);


    }


}


