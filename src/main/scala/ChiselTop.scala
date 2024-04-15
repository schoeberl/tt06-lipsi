import chisel3._
import chisel3.util._

import lipsi._

class Decoder extends Module {

  val counter = IO(Input(UInt(4.W)))
  val segments = IO(Output(UInt(7.W)))

  segments := 0.U
  switch (counter) {
    is(0.U) {
      segments := "b0111111".U
    }
    is(1.U) {
      segments := "b0000110".U
    }
    is(2.U) {
      segments := "b1011011".U
    }
    is(3.U) {
      segments := "b1001111".U
    }
    is(4.U) {
      segments := "b1100110".U
    }
    is(5.U) {
      segments := "b1101101".U
    }
    is(6.U) {
      segments := "b1111100".U
    }
    is(7.U) {
      segments := "b0000111".U
    }
    is(8.U) {
      segments := "b1111111".U
    }
    is(9.U) {
      segments := "b1100111".U
    }
    is (10.U) { segments := "b1110111".U }
    is (11.U) { segments := "b1111100".U }
    is (12.U) { segments := "b0111001".U }
    is (13.U) { segments := "b1011110".U }
    is (14.U) { segments := "b1111001".U }
    is (15.U) { segments := "b1110001".U }
  }
}
/**
 * Example design in Chisel.
 * A redesign of the Tiny Tapeout example.
 */
class ChiselTop() extends Module {
  val io = IO(new Bundle {
    val ui_in = Input(UInt(8.W))      // Dedicated inputs
    val uo_out = Output(UInt(8.W))    // Dedicated outputs
    val uio_in = Input(UInt(8.W))     // IOs: Input path
    val uio_out = Output(UInt(8.W))   // IOs: Output path
    val uio_oe = Output(UInt(8.W))    // IOs: Enable path (active high: 0=input, 1=output)
    val ena = Input(Bool())           // will go high when the design is enabled
  })

  io.uio_out := 0.U
  // use bi-directionals as input
  io.uio_oe := 0.U

  val add = WireDefault(0.U(7.W))
  add := io.ui_in + io.uio_in

  val lipsi = Module(new Lipsi("lipsi/asm/blink.asm", memSize = 4))
  lipsi.io.din := add

  // Blink with 1 Hzq
  val cntReg = RegInit(0.U(32.W))
  val ledReg = RegInit(0.U(1.W))
  cntReg := cntReg + 1.U
  when (cntReg === 25000000.U) {
    cntReg := 0.U
    ledReg := ~ledReg
  }
  val dec = Module(new Decoder)
  dec.counter := lipsi.io.dout(3, 0)
  val seg = dec.segments
  val led = Mux(io.ui_in(0), ledReg, 0.U)

  io.uo_out := led ## seg
  // io.uo_out := ledReg ## lipsi.io.dout(6, 0)
}

object ChiselTop extends App {
  emitVerilog(new ChiselTop(), Array("--target-dir", "src"))
}