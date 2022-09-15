
/**
 * Copyright 2021 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.up.fe.comp.Jasmin.JasminEmitter;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsStrings;

import java.util.Collections;

public class BackendTest {

     @Test
     public void testHelloWorld3() {
     String result = SpecsIo.getResource("fixtures/public/cp2/OllirToJasminBasic.ollir");
    // TestUtils.noErrors(result.getReports());
     //var output = result.run();
     //assertEquals("Hello, World!", output.trim());
         JasminEmitter jasminEmitter = new JasminEmitter();
         JasminResult jasminResult=jasminEmitter.toJasmin(new OllirResult(result, Collections.emptyMap()));
         jasminResult.run();
     }
    @Test
    public void testHelloWorld() {

        String jasminCode = SpecsIo.getResource("fixtures/public/jasmin/HelloWorld.j");
        var output = TestUtils.runJasmin(jasminCode);
        assertEquals("Hello World!\nHello World Again!\n", SpecsStrings.normalizeFileContents(output));
    }
}
