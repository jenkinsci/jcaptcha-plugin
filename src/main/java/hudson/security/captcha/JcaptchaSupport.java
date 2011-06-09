/*
 * The MIT License
 *
 * Copyright 2011 Winston.Prakash@oracle.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.security.captcha;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import hudson.Extension;
import hudson.security.captcha.CaptchaSupport;
import hudson.security.captcha.CaptchaSupportDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Captcha Support for Hudson Security Realm Using http://jcaptcha.sourceforge.net/
 * @author Winston Prakash
 */
public class JcaptchaSupport extends CaptchaSupport {

    private static final Logger LOGGER = Logger.getLogger(JcaptchaSupport.class.getName());

    @DataBoundConstructor
    public JcaptchaSupport() {
    }

    /**
     * {@link DefaultManageableImageCaptchaService} holder to defer initialization.
     */
    private static final class CaptchaService {
        private static final DefaultManageableImageCaptchaService INSTANCE = new DefaultManageableImageCaptchaService();
    }

    /**
     * Validates the captcha.
     */
    @Override
    public  boolean validateCaptcha(String id, String text) {
        try {
            Boolean b = CaptchaService.INSTANCE.validateResponseForID(id, text);
            return b != null && b;
        } catch (CaptchaServiceException e) {
            LOGGER.log(Level.INFO, "Captcha validation had a problem", e);
            return false;
        }
    }
    
     /**
     * Generates a captcha image.
     */
    public void generateImage(String id, OutputStream ios) throws IOException {
        ImageIO.write( CaptchaService.INSTANCE.getImageChallengeForID(id), "PNG", ios );
    }

    @Extension
    public static class DescriptorImpl extends CaptchaSupportDescriptor {

        @Override
        public String getDisplayName() {
            return "JCaptcha";
        }
    }
}
