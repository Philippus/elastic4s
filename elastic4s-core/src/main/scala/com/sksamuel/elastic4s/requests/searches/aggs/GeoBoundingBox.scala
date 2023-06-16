package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.GeoPoint

case class GeoBoundingBox(topLeft: GeoPoint, bottomRight: GeoPoint)
