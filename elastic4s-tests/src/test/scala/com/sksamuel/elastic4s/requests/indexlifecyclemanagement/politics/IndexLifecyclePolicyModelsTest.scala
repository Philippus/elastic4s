package com.sksamuel.elastic4s.requests.indexlifecyclemanagement.politics

import com.sksamuel.elastic4s.handlers.indexlifecyclemanagement.IndexLifecyclePolicyContentBuilder
import com.sksamuel.elastic4s.json.{IntValue, ObjectValue, RawValue, StringValue}
import com.sksamuel.elastic4s.requests.indexlifecyclemanagement.policy.IndexLifecyclePolicy
import com.sksamuel.elastic4s.requests.indexlifecyclemanagement.policy.IndexLifecyclePolicyAction.{
  DeleteAction,
  ForceMergeAction
}
import com.sksamuel.elastic4s.requests.indexlifecyclemanagement.policy.IndexLifecyclePolicyPhase.{
  DeletePhase,
  WarmPhase
}
import com.sksamuel.elastic4s.requests.indexlifecyclemanagement.{GetIndexLifecyclePolicyResponse, InUseBy}
import com.sksamuel.elastic4s.{JacksonSupport, JsonSugar}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

import scala.io.Source

class IndexLifecyclePolicyModelsTest extends AnyFlatSpec with MockitoSugar with JsonSugar with Matchers {
  "the create lifecycle policy dsl" should "generate the correct json" in {

    val policy = IndexLifecyclePolicy("testPolicy")
      .withMeta(
        "description" -> StringValue("used for nginx log"),
        "project"     ->
          ObjectValue.empty
            .putValue("name", StringValue("myProject"))
            .putValue("department", StringValue("myDepartment"))
      )
      .withPhases(
        WarmPhase
          .withSettings("min_age" -> StringValue("10d"))
          .withActions(
            ForceMergeAction.withSettings("max_num_segments" -> IntValue(1))
          ),
        DeletePhase
          .withSettings("min_age" -> StringValue("30d"))
          .withActions(
            DeleteAction
          )
      )

    IndexLifecyclePolicyContentBuilder(policy).string should matchJsonResource(
      "/json/indexlifecyclemanagement/test_policy_create.json"
    )
  }

  "the create lifecycle policy dsl" should "parse json correctly" in {
    val resourceName = "/json/indexlifecyclemanagement/test_policy_get.json"
    val patternValue = GetIndexLifecyclePolicyResponse(
      version = 1,
      modifiedDate = 82392349,
      policy = IndexLifecyclePolicy("my_policy")
        .withPhases(
          WarmPhase
            .withSettings("min_age" -> StringValue("10d"))
            .withActions(ForceMergeAction.withSettings("max_num_segments" -> StringValue("1"))),
          DeletePhase
            .withSettings("min_age" -> StringValue("30d"))
            .withActions(DeleteAction.withSettings("delete_searchable_snapshot" -> StringValue("true")))
        ),
      inUseBy = Some(InUseBy(Nil, Nil, Nil))
    )

    val jsonResourceStream = getClass.getResourceAsStream(resourceName)
    withClue(s"expected JSON resource [$resourceName] ") {
      jsonResourceStream should not be null
    }
    val source             = Source.fromInputStream(jsonResourceStream)
    val jsonReference      =
      try
        source.mkString
      finally
        source.close()

    GetIndexLifecyclePolicyResponse.deserialize(JacksonSupport.mapper.readTree(jsonReference)) shouldBe patternValue
  }
}
