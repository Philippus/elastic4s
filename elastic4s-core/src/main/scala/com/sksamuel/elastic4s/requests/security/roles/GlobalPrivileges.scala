package com.sksamuel.elastic4s.requests.security.roles

case class GlobalPrivileges(application: ManagePrivileges)

object GlobalPrivileges {
	def apply(applications: Seq[String]): GlobalPrivileges = {
		GlobalPrivileges(
			ManagePrivileges(
				ManageApplicationPrivileges(applications)
			)
		)
	}
}

case class ManagePrivileges(manage: ManageApplicationPrivileges)

case class ManageApplicationPrivileges(applications: Seq[String])