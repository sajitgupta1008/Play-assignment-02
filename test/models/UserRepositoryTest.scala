package models

import org.scalatestplus.play.PlaySpec

class UserRepositoryTest extends PlaySpec {

  val modelsTest = new ModelsTest[UserRepository]

  private val userData = UserData(0,"sajit",None,"gupta","sajit.gupta@gmail.com","qwerty123"
  ,8743922586L,"male",23)

  "UserRepository" should{

    "store data" in {
      val result  = modelsTest.result(modelsTest.repository.addUser(userData))
      result mustEqual true
    }
  }

}