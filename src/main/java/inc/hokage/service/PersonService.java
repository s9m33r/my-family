package inc.hokage.service;

import inc.hokage.model.Person;
import inc.hokage.model.Relationship;
import org.neo4j.driver.*;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.neo4j.driver.Values.parameters;

@Service
public class PersonService implements AutoCloseable {
    private final Driver driver;

    public PersonService() {
        driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "dummyPassword"));
    }

    @Override
    public void close() {
        driver.close();
    }

    public long add(Person person) {
        long personId;

        try (Session session = driver.session()) {
            personId = session.writeTransaction(tx -> {
                Result result = tx.run("CREATE (p:Person) " +
                                "SET p.name = $name, p.nickName = $nickName, " +
                                "p.occupation = $occupation, p.occupationStatus = $occupationStatus, " +
                                "p.city = $city, p.country = $country, p.alive=$alive " +
                                "RETURN id(p)",
                        parameters("name",
                                person.getName(),
                                "nickName",
                                person.getNickName(),
                                "occupation",
                                person.getOccupation(),
                                "occupationStatus",
                                person.getOccupationStatus().toString(),
                                "city",
                                person.getCity(),
                                "country",
                                person.getCountry(),
                                "alive",
                                person.isAlive()));

                return result.single().get(0).asLong();
            });
        }

        return personId;
    }

    public long remove(long id) {
        long deleted_id;

        try (Session session = driver.session()) {
            deleted_id = session.writeTransaction(tx -> {
                Result result = tx.run("match (p:Person) where ID(p) = $id " +
                        "with p, ID(p) as p_id " +
                        "delete p " +
                        "return p_id",
                        parameters("id", id));

                return result.single().get(0).asLong();
            });
        }

        return deleted_id;
    }

    public long addToFamily(long personId, long familyId) {
        long relationshipId;

        try (Session session = driver.session()) {
            relationshipId = session.writeTransaction(tx -> {
                Result result = tx.run("match (p:Person), (f:Family) where ID(p) = $personId and ID(f) = $familyId " +
                                "merge (p)-[m:MEMBER_OF]->(f) " +
                                "return ID(m)",
                        parameters("personId", personId, "familyId", familyId));

                return result.single().get(0).asLong();
            });
        }

        return relationshipId;
    }

    public long removeFromFamily(long personId, long familyId) {
        long relationshipId;

        try (Session session = driver.session()) {
            relationshipId = session.writeTransaction(tx -> {
                Result result = tx.run("match (p:Person)-[m:MEMBER_OF]->(f:Family) where ID(p) = $personId and ID(f) = $familyId " +
                                "with m, ID(m) as memberOfId " +
                                "delete m " +
                                "return memberOfId",
                        parameters("personId", personId, "familyId", familyId));

                return result.list(record -> record.get(0).asLong()).get(0);
            });
        }

        return relationshipId;
    }

    public long addRelationship(long firstPersonId, Relationship relationship, long secondPersonId) {
        long relationshipId;

        try (Session session = driver.session()) {
            relationshipId = session.writeTransaction(tx -> {
                Result result = tx.run("match (firstPerson:Person), (secondPerson:Person) where ID(firstPerson) = $firstPersonId and ID(secondPerson) = $secondPersonId " +
                                "merge (firstPerson)-[relationship:" + relationship.toString() + "]->(secondPerson) " +
                                "return ID(relationship)",
                        parameters("firstPersonId", firstPersonId, "secondPersonId", secondPersonId));

                return result.list(record -> record.get(0).asLong()).get(0);
            });
        }

        return relationshipId;
    }

    public List<String> getCousins(long personId) {
        List<String> cousinNames;

        try (Session session = driver.session()) {
            cousinNames = session.writeTransaction(tx -> {
                Result result = tx.run("match (p:Person)-[:SON_OF]->(father:Person)-[:BROTHER_OF]->(fathersBrother:Person)" +
                                "-[:FATHER_OF]->(cousin:Person) where id(p) = $personId " +
                                "return cousin.name",
                        parameters("personId", personId));

                return result.list(record -> record.get(0).asString());
            });
        }

        return cousinNames;
    }
}
