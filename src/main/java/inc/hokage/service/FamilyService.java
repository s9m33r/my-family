package inc.hokage.service;

import inc.hokage.model.Family;
import org.neo4j.driver.*;
import org.springframework.stereotype.Service;

import static org.neo4j.driver.Values.parameters;

@Service
public class FamilyService implements AutoCloseable{
    private final Driver driver;

    public FamilyService() {
        driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "dummyPassword"));
    }

    @Override
    public void close() {
        driver.close();
    }

    public Family add(Family family) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                Result result = tx.run("CREATE (f:Family) " +
                                "SET f.name = $name " +
                                "RETURN f.name + ', from node ' + id(f)",
                        parameters("name",
                                family.getName()));

                return result.single().get(0).asString();
            });
        }

        return family;
    }

    public long remove(long id) {
        long deleted_id;

        try (Session session = driver.session()) {
            deleted_id = session.writeTransaction(tx -> {
                Result result = tx.run("match (f:Family) where ID(f) = $id " +
                                "with f, ID(f) as f_id " +
                                "delete f " +
                                "return f_id",
                        parameters("id", id));

                return result.single().get(0).asLong();
            });
        }

        return deleted_id;
    }
}
