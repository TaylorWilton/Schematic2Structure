import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Tests {
    @Test
    public void TestSizeValidation(){
        Assert.assertFalse("validation should fail as height is greater than 32",Schematic2Structure.validateStructure(33,2,1));
        Assert.assertFalse("validation should fail as width is greater than 32",Schematic2Structure.validateStructure(3,34,1));
        Assert.assertFalse("validation should fail as length is greater than 32",Schematic2Structure.validateStructure(3,2,100));

        Assert.assertTrue("validation should pass as all dimensions are less than 32",Schematic2Structure.validateStructure(15,8,9));
        Assert.assertTrue("validation should pass as all dimensions are less than or equal to 32",Schematic2Structure.validateStructure(32,12,9));
        Assert.assertTrue("validation should pass as all dimensions are less than 32",Schematic2Structure.validateStructure(31,24,12));
        Assert.assertTrue("validation should pass as all dimensions are less than 32",Schematic2Structure.validateStructure(1,8,9));

        Assert.assertFalse("validation should fail as there is a negative number", Schematic2Structure.validateStructure(-3,5,2));
        Assert.assertFalse("validation should fail as there is a negative number", Schematic2Structure.validateStructure(3,-5,2));
        Assert.assertFalse("validation should fail as there is a negative number", Schematic2Structure.validateStructure(3,5,-2));

        Assert.assertTrue("validation should pass as all dimensions are equal to 32 (the limit)", Schematic2Structure.validateStructure(32,32,32));
        Assert.assertFalse("validation should fail as all dimensions are greater than 32", Schematic2Structure.validateStructure(33,33,33));

    }

    @Test
    public void TestFileNameValidation(){
        Assert.assertTrue("tests not yet implemented", false);
    }
    @Test
    public void TestTooLargeFile(){
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

         Schematic2Structure.main(new String[]{"schematics/luxury-house.schematic"});
        // includes CRLF
        String expected = "Structure is too large!\r\n";

        Assert.assertEquals("Program should notify user than structure is too large", outContent.toString(),expected);
    }

}
