package com.sksamuel.elastic4s.handlers.index

import com.sksamuel.elastic4s.handlers.fields.ElasticFieldBuilderFn
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.DynamicTemplateRequest

object DynamicTemplateBuilderFn {

  /** Deserializes a single dynamic template from the ES "array of single-key objects" format.
    *
    * ES encodes dynamic_templates as: [ { "template_name": { "match": "...", "mapping": {...} } }, ... ]
    *
    * @param name   the template name (the single key in the outer object)
    * @param values the inner object containing match conditions and the "mapping" sub-object
    */
  def fromMap(name: String, values: Map[String, Any]): DynamicTemplateRequest = {
    val mappingMap = values.get("mapping") match {
      case Some(m: Map[_, _]) => m.asInstanceOf[Map[String, Any]]
      case _                  => Map.empty[String, Any]
    }
    val field = ElasticFieldBuilderFn.construct(name, mappingMap)

    DynamicTemplateRequest(
      name             = name,
      mapping          = field,
      `match`          = values.get("match").map(_.toString),
      unmatch          = values.get("unmatch").map(_.toString),
      pathMatch        = values.get("path_match").map(_.toString),
      pathUnmatch      = values.get("path_unmatch").map(_.toString),
      MatchPattern     = values.get("match_pattern").map(_.toString),
      matchMappingType = values.get("match_mapping_type").map(_.toString)
    )
  }

  /** Converts the raw mappings map (from Jackson untyped deserialization) into a typed [[TemplateMappings]].
    *
    * Handles the ES dynamic_templates encoding (array of single-key objects) and reconstructs
    * [[com.sksamuel.elastic4s.requests.mappings.dynamictemplate.DynamicTemplateRequest]] instances.
    */
  def fromMappingsMap(raw: Map[String, Any]): TemplateMappings = {
    val dynamicTemplates: Seq[DynamicTemplateRequest] = raw.get("dynamic_templates") match {
      case Some(list: List[_]) =>
        list.flatMap {
          case entry: Map[_, _] =>
            entry.asInstanceOf[Map[String, Any]].map { case (templateName, value) =>
              fromMap(templateName, value.asInstanceOf[Map[String, Any]])
            }
          case _ => Seq.empty
        }
      case _ => Seq.empty
    }

    val properties = raw.get("properties") match {
      case Some(props: Map[_, _]) =>
        props.asInstanceOf[Map[String, Any]].map { case (fieldName, value) =>
          ElasticFieldBuilderFn.construct(fieldName, value.asInstanceOf[Map[String, Any]])
        }.toSeq
      case _ => Seq.empty
    }

    TemplateMappings(dynamicTemplates, properties)
  }
}
