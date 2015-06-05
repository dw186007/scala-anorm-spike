import anorm.{NotAssigned, Pk}

case class Favorite(id: Pk[Long] = NotAssigned, name: String, url: String )

/**
 * Helper for pagination
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long){
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Computer {
  
}