package org.neo4j.lab.cypher

import commands._
import org.junit.Test
import org.junit.Assert._
import org.neo4j.graphdb.Direction


/**
 * Created by Andres Taylor
 * Date: 5/1/11
 * Time: 10:36 
 */

class CypherParserTest {
  def testQuery(query: String, expectedQuery: Query) {
    val parser = new CypherParser()
    val executionTree = parser.parse(query).get

    assertEquals(expectedQuery, executionTree)
  }

  @Test def shouldParseEasiestPossibleQuery() {
    testQuery(
      "from start = NODE(1) select start",
      Query(
        Select(EntityOutput("start")),
        From(NodeById("start", 1))))
  }

  @Test def shouldParseMultipleNodes() {
    testQuery(
      "from start = NODE(1,2,3) select start",
      Query(
        Select(EntityOutput("start")),
        From(NodeById("start", 1, 2, 3))))
  }

  @Test def shouldParseMultipleInputs() {
    testQuery(
      "from a = NODE(1), b = NODE(2) select a,b",
      Query(
        Select(EntityOutput("a"), EntityOutput("b")),
        From(NodeById("a", 1), NodeById("b", 2))))
  }

  @Test def shouldFilterOnProp() {
    testQuery(
      "from a = NODE(1) where a.name = \"andres\" select a",
      Query(
        Select(EntityOutput("a")),
        From(NodeById("a", 1)),
        Some(Where(StringEquals("a", "name", "andres"))))
    )
  }

  @Test def multipleFilters() {
    testQuery(
      "from a = NODE(1) where a.name = \"andres\" OR a.name = \"mattias\" select a",
      Query(
        Select(EntityOutput("a")),
        From(NodeById("a", 1)),
        Some(Where(Or(
          StringEquals("a", "name", "andres"), StringEquals("a", "name", "mattias")
        ))))
    )
  }

  @Test def relatedTo() {
    testQuery(
      "from a = NODE(1), (a) -['knows']-> (b) select a, b",
      Query(
        Select(EntityOutput("a"), EntityOutput("b")),
        From(NodeById("a", 1), RelatedTo("a", "b", "r", "knows", Direction.OUTGOING)))
    )
  }

  @Test def relatedToWithRelationOutput() {
    testQuery(
      "from a = NODE(1), (a) -[rel,'knows']-> (b) select rel",
      Query(
        Select(EntityOutput("rel")),
        From(NodeById("a", 1), RelatedTo("a", "b", "rel", "knows", Direction.OUTGOING)))
    )
  }
}
