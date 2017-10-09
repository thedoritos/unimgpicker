//
//  UIImage+FixOrientation.m
//  Unity-iPhone
//
//  Created by thedoritos on 2017/10/09.
//

#import "UIImage+FixOrientation.h"

@implementation UIImage(FixOrientation)

- (UIImage *)imageWithFixedOrientation {
    if (self.imageOrientation == UIImageOrientationUp) {
        return self;
    }

    UIGraphicsBeginImageContextWithOptions(self.size, NO, self.scale);
    [self drawInRect:CGRectMake(0, 0, self.size.width, self.size.height)];
    UIImage *fixedImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return fixedImage;
}

@end
