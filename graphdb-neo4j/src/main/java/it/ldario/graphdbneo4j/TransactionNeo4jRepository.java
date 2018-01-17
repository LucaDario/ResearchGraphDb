package it.ldario.graphdbneo4j;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;

@Component
interface TransactionNeo4jRepository extends Neo4jRepository<TransactionNeo4j,Long>{



}
