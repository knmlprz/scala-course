enum Color:
  case Grey, Brown

case class Cat(striped: Boolean, color: Color)

val brownStripedCat = Cat(true, Color.Brown)
val brownCat = Cat(false, Color.Brown)
val greyStripedCat = Cat(true, Color.Grey)
val greyCat = Cat(false, Color.Grey)

def addStripes(newCat: Cat): Cat =
  newCat.copy(striped = true)

def removeStripes(newCat: Cat): Cat =
  newCat.copy(striped = false)

def makeGrey(newCat: Cat): Cat =
  newCat.copy(color = Color.Grey)

def makeBrown(newCat: Cat): Cat =
  newCat.copy(color = Color.Brown)

def catToDog(cat: Cat): Dog =
  Dog(cat.striped, cat.color)

case class mat2x2(a11: Float, a12: Float,
                  a21: Float, a22: Float)

def matmul(leftMat: mat2x2)(rightMat: mat2x2): mat2x2 =
  mat2x2(leftMat.a11*rightMat.a11 + leftMat.a12*rightMat.a21, leftMat.a11*rightMat.a12 + leftMat.a12*rightMat.a22,
         leftMat.a21*rightMat.a21 + leftMat.a22*rightMat.a21, leftMat.a21*rightMat.a12 + leftMat.a22*rightMat.a22)

// show functors
case class Dog(striped: Boolean, color: Color)

def addStripes(newDog: Dog): Dog =
  newDog.copy(striped = true)

def removeStripes(newDog: Dog): Dog =
  newDog.copy(striped = false)

def makeGrey(newDog: Dog): Dog =
  newDog.copy(color = Color.Grey)

def makeBrown(newDog: Dog): Dog =
  newDog.copy(color = Color.Brown)

@main def hello: Unit =
  println("Hello world!")
  println(brownCat)
  println(addStripes(brownCat))
  val idMat = mat2x2(1, 0,
                     0, 1)
  val twoMat = mat2x2(2, 0,
                      0, 2)
  val fancyMat  = mat2x2(2, 3,
                         0, 2)
  val fancyMat2 = mat2x2(3, 0,
                         3, 2)
  println(idMat)
  println(matmul(idMat)(idMat))
  println(matmul(twoMat)(twoMat))
  println(matmul(twoMat)(idMat))
  println(matmul(fancyMat)(twoMat))
  println(matmul(fancyMat)(fancyMat))
  println(matmul(fancyMat)(fancyMat2))
  println(matmul(fancyMat2)(fancyMat))
