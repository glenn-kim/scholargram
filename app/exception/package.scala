/**
 * Created by infinitu on 15. 1. 27..
 */
package object exception {
  class InvalidateParameterException(message:String) extends Exception(message)
  class InvalidDataIntegraityException(msessage:String) extends Exception(msessage)
  class DoesNotHavePermissionException(msessage:String) extends Exception(msessage)
  class NoSuchRowException(msessage:String) extends Exception(msessage)
  class AlreadyRegistrated(msessage:String) extends Exception(msessage)
}
