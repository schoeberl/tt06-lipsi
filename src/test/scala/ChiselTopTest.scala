import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
class ChiselTopTest extends AnyFlatSpec with ChiselScalatestTester {

  "ChiselTop" should "pass" in {
    // test(new ChiselTop()).withAnnotations(Seq(WriteVcdAnnotation, VerilatorBackendAnnotation)) { dut =>
    test(new ChiselTop()).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.io.ui_in.poke(1.U)
      dut.io.uio_in.poke(2.U)
      dut.io.ena.poke(true.B)
      dut.clock.setTimeout(0)
      dut.clock.step(1000)
    }
  }
}
