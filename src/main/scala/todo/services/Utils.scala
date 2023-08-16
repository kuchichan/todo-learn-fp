package todo.services

object Utils {
  def removeFromVectorAtIndex[T](v: Vector[T], index: Int): Vector[T] = {  
    val (v1, v2) = v.splitAt(index) 
    v1 ++ v2.tail
  } 

}
