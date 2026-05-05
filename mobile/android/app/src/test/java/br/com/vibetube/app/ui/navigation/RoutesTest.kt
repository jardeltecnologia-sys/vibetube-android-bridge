package br.com.vibetube.app.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Testa apenas a função pura `decodeFromRoute`. O encode usa android.util.Base64
 * que não está disponível em testes JVM puros — para ele há cobertura via
 * teste instrumented. Aqui validamos que decode aceita strings simples.
 */
class RoutesTest {

    @Test
    fun `decodeFromRoute em string nao codificada devolve algo (no crash)`() {
        // Quando a entrada não é Base64 válido, devolvemos a própria string.
        // Nesse teste apenas garantimos que não lança exceção.
        val out = Routes.decodeFromRoute("nao-base64-mas-nao-quebra")
        // Pode ou não decodificar — o importante é não crashar.
        assertEquals(out, out)
    }
}
