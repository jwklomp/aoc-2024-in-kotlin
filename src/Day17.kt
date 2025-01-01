import kotlin.math.pow

private data class Registers(val A: Int, val B: Int, val C: Int)

fun main() {

    fun evaluateOperand(operand: Int, registers: Registers): Int = when (operand) {
        in 0..3 -> operand // Literal values 0–3
        4 -> registers.A
        5 -> registers.B
        6 -> registers.C
        else -> 0 // Operand 7 is invalid and should never occur
    }

    fun runChronospatialComputer(program: List<Int>, initialRegisters: Registers): String {
        tailrec fun doInstruction(
            pointer: Int,
            registers: Registers,
            output: List<Int>
        ): List<Int> {
            if (pointer >= program.size) {
                println("Program halted.")
                return output
            }

            val opcode = program[pointer]
            val operand = program.getOrNull(pointer + 1) ?: 0
            println("Executing opcode: $opcode, operand: $operand at pointer: $pointer")
            println("Current Registers: A=${registers.A}, B=${registers.B}, C=${registers.C}")

            val nextPointer = pointer + 2

            val (updatedRegisters, updatedPointer, newOutput) = when (opcode) {
                0 -> { // adv: Divide A by 2^operand
                    val divisor = 2.0.pow(evaluateOperand(operand, registers).toDouble()).toInt()
                    if (divisor == 0) {
                        println("Division by zero in adv. Skipping.")
                        Triple(registers, nextPointer, output)
                    } else {
                        Triple(registers.copy(A = registers.A / divisor), nextPointer, output)
                    }
                }

                1 -> { // bxl: XOR B with literal operand
                    Triple(registers.copy(B = registers.B xor operand), nextPointer, output)
                }

                2 -> { // bst: Modulo operand by 8 and store in B
                    Triple(registers.copy(B = evaluateOperand(operand, registers) % 8), nextPointer, output)
                }

                3 -> { // jnz: Jump to operand if A != 0
                    if (registers.A != 0) Triple(registers, operand, output)
                    else Triple(registers, nextPointer, output)
                }

                4 -> { // bxc: XOR B with C
                    Triple(registers.copy(B = registers.B xor registers.C), nextPointer, output)
                }

                5 -> { // out: Output operand modulo 8
                    Triple(registers, nextPointer, output + (evaluateOperand(operand, registers) % 8))
                }

                6 -> { // bdv: Divide A by 2^operand and store in B
                    val divisor = 2.0.pow(evaluateOperand(operand, registers).toDouble()).toInt()
                    if (divisor == 0) {
                        println("Division by zero in bdv. Skipping.")
                        Triple(registers, nextPointer, output)
                    } else {
                        Triple(registers.copy(B = registers.A / divisor), nextPointer, output)
                    }
                }

                7 -> { // cdv: Divide A by 2^operand and store in C
                    val divisor = 2.0.pow(evaluateOperand(operand, registers).toDouble()).toInt()
                    if (divisor == 0) {
                        println("Division by zero in cdv. Skipping.")
                        Triple(registers, nextPointer, output)
                    } else {
                        Triple(registers.copy(C = registers.A / divisor), nextPointer, output)
                    }
                }

                else -> { // Invalid opcode
                    println("Invalid opcode: $opcode. Skipping.")
                    Triple(registers, nextPointer, output)
                }
            }

            println("Updated Registers: A=${updatedRegisters.A}, B=${updatedRegisters.B}, C=${updatedRegisters.C}")
            println("Output so far: ${newOutput.joinToString(",")}")
            println("Next Pointer: $updatedPointer")
            println("-----")

            return doInstruction(updatedPointer, updatedRegisters, newOutput)
        }

        return doInstruction(0, initialRegisters, emptyList()).joinToString(",")
    }

    fun part1(): String {
        //val initialRegisters = Registers(A = 729, B = 0, C = 0)
        val initialRegisters = Registers(A = 59590048, B = 0, C = 0)
        //val program = listOf(0, 1, 5, 4, 3, 0)
        val program = listOf(2, 4, 1, 5, 7, 5, 0, 3, 1, 6, 4, 3, 5, 5, 3, 0)
        return runChronospatialComputer(program, initialRegisters)
    }

    fun part2(): Int {
        return 1
    }

    part1().println()
    //part2().println()

    //part1().println()
    //part2().println()
}
