package org.eterk.app


import java.io.{BufferedWriter, File, FileWriter, OutputStreamWriter}
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.math.BigDecimal
import java.math.RoundingMode

object SavingsCalculator {


  object FinancialPlan {

    val FULI: String = "复利"

    val DANLI: String = "单利"

    val LCZQ_FULI: String = "零存整取复利"


  }


  case class FinancialPlan(principal: Double, rate: Double, term: Double, year: Int, tpe: String) {

    import FinancialPlan._

    def result: Seq[(Int, Double)] = {
      tpe match {
        case FULI => calculateCompoundInterest
        case DANLI => calculateSimpleInterest
        case LCZQ_FULI => calculateCompoundInterestWithAnnualDeposit
      }
    }


    private def calculateCompoundInterest: Seq[(Int, Double)] = {
      var amount = principal
      val periods = (year / term).toInt
      (1 to periods).map { year =>
        amount = amount * Math.pow(1 + rate * term, 1)
        (year, amount - principal)
      }
    }

    private def calculateSimpleInterest: Seq[(Int, Double)] = {
      var amount = 0.0
      val periods = (year / term).toInt
      (1 to periods).map { year =>
        amount += principal * rate * term
        (year, amount)
      }
    }

    private def calculateCompoundInterestWithAnnualDeposit: Seq[(Int, Double)] = {
      val annualDeposit = principal / term
      var total = BigDecimal.valueOf(0.0)
      (1 to year).map { year =>
        if (year <= term) {
          total = total.add(BigDecimal.valueOf(annualDeposit))
        }
        total = total.multiply(BigDecimal.valueOf(1 + rate))
        (year, total.doubleValue())
      }
    }


  }


  def main(args: Array[String]): Unit = {
    val interestRates = Seq(0.019, 0.0195, 0.017, 0.024, 0.035, 0.03) // 年利率
    val principals = Seq(100.0) // 本金
    val types = Seq("复利", "单利", "零存整取复利") // 存储类型
    val terms = Seq(0.5, 1, 2, 3) // 存储期限（年）
    val years = Seq(3, 5, 10) // 存储年限
    val startDate = LocalDate.now() // 存款开始时间
    val fileName = "收益情况.csv" // 输出文件名

    val res = calculateResultsDetail(FinancialPlan(40, 0.03, 10, 50, FinancialPlan.LCZQ_FULI) :: Nil, startDate)

    writeCSV(res, "保险.csv")

    val results = calculateResults(combine(interestRates, principals, types, terms, years), startDate)
    writeCSV(results, fileName)
  }

  import java.math.BigDecimal
  import java.math.RoundingMode

  def truncateToTwoDecimalPlaces(value: Double): Double = {
    val bd = BigDecimal.valueOf(value)
    bd.setScale(2, RoundingMode.DOWN).doubleValue()
  }

  def combine(interestRates: Seq[Double],
              principals: Seq[Double],
              types: Seq[String],
              terms: Seq[Double],
              years: Seq[Int]): Seq[FinancialPlan] = {
    for {
      rate <- interestRates
      principal <- principals
      t <- types
      term <- terms
      year <- years
    } yield FinancialPlan(principal, rate, term, year, t)

  }

  // get detail
  def calculateResultsDetail(plans: Seq[FinancialPlan],
                             startDate: LocalDate): Seq[Seq[String]] = {

    Seq("本金", "年利率", "存储类型", "存储期限", "存储年限", "年份", "到期利息", "到期时间") +:
      plans.flatMap(fp => {

        import fp._

        fp.result.map(r => {
          val maturityDate = startDate.plusYears(r._1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
          Seq(principal.toString, rate.toString, tpe, term.toString, year.toString, r._1.toString, truncateToTwoDecimalPlaces(r._2).toString, maturityDate)
        }

        )


      })

  }


  // get final
  def calculateResults(plans: Seq[FinancialPlan],
                       startDate: LocalDate): Seq[Seq[String]] = {
    Seq("本金", "年利率", "存储类型", "存储期限", "存储年限", "到期利息", "到期时间") +:
      plans.map(fp => {
        val maturityDate = startDate.plusYears(fp.year).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        import fp._
        Seq(principal.toString, rate.toString, tpe, term.toString, year.toString, truncateToTwoDecimalPlaces(fp.result.last._2).toString, maturityDate)
      })

  }


  def writeCSV(data: Seq[Seq[String]], fileName: String): Unit = {
    val bom = "\uFEFF"
    val file = new BufferedWriter(new FileWriter(new File(fileName), StandardCharsets.UTF_8))
    file.write(bom) // 写入 BOM
    data.foreach { row =>
      file.write(row.mkString(",") + "\n")
    }
    file.close()
  }

}
