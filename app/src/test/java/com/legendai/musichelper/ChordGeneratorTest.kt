import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Test
import com.legendai.musichelper.util.ChordGenerator

class ChordGeneratorTest {
    @Test
    fun `parseKey handles major and minor`() {
        val result = ChordGenerator.suggest("C minor", "rock")
        assertTrue(result.isNotEmpty())
        val resultMajor = ChordGenerator.suggest("A", "rock")
        assertTrue(resultMajor.isNotEmpty())
    }

    @Test
    fun `invalid key returns empty list`() {
        assertTrue(ChordGenerator.suggest("H", "rock").isEmpty())
    }
}
