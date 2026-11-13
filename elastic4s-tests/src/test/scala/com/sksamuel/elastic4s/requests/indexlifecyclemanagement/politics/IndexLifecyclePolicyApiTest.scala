package com.sksamuel.elastic4s.requests.indexlifecyclemanagement.politics

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.json.{RawValue, StringValue}
import com.sksamuel.elastic4s.requests.indexlifecyclemanagement.policy.{
  IndexLifecyclePolicy,
  IndexLifecyclePolicyAction
}
import com.sksamuel.elastic4s.requests.indexlifecyclemanagement.policy.IndexLifecyclePolicyPhase.DeletePhase
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.Eventually
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class IndexLifecyclePolicyApiTest extends AnyFlatSpec with Matchers with ElasticDsl with DockerTests with Eventually
    with BeforeAndAfterAll {
  "index lifecycle policy delete" should "be success on empty elasticsearch" in {
    client.execute(
      deleteIndexLifecyclePolicy("my_policy")
    ).await.result.acknowledged shouldBe false
  }

  "index lifecycle policy" should "be created" in {
    client.execute(
      deleteIndexLifecyclePolicy("my_policy")
    ).await.result

    val policy = IndexLifecyclePolicy("my_policy")
      .withPhases(
        DeletePhase
          .withSettings("min_age" -> StringValue("30d"))
          .withActions(IndexLifecyclePolicyAction.DeleteAction)
      )

    val result =
      client.execute(
        createIndexLifecyclePolicy(policy)
      ).await.result

    result.acknowledged shouldBe true
  }

  "index lifecycle policy" should "be found" in {
    client.execute(
      deleteIndexLifecyclePolicy("my_policy")
    ).await

    val policy = IndexLifecyclePolicy("my_policy")
      .withPhases(
        DeletePhase
          .withSettings("min_age" -> StringValue("30d"))
          .withActions(
            IndexLifecyclePolicyAction.DeleteAction
              .withSettings("delete_searchable_snapshot" -> StringValue("true"))
          )
      )

    client.execute(
      createIndexLifecyclePolicy(policy)
    ).await

    val policyFromElastic = client.execute(
      getIndexLifecyclePolicy("my_policy")
    ).await.result

    policyFromElastic.map(_.policy) shouldBe Some(policy)
  }

  "index lifecycle policy" should "be deleted" in {
    client.execute(
      deleteIndexLifecyclePolicy("my_policy")
    ).await

    val policy = IndexLifecyclePolicy("my_policy")

    client.execute(
      createIndexLifecyclePolicy(policy)
    ).await

    client.execute(
      deleteIndexLifecyclePolicy("my_policy")
    ).await

    client.execute(
      getIndexLifecyclePolicy("my_policy")
    ).await.result shouldBe None
  }

  override def afterAll(): Unit = {
    client.execute(
      deleteIndexLifecyclePolicy("my_policy")
    ).await
  }

}
