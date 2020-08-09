//
//  Picker.h
//  Unity-iPhone
//
//  Created by thedoritos on 11/19/16.
//
//

#import <UIKit/UIKit.h>

@interface Picker : NSObject<UIImagePickerControllerDelegate, UINavigationControllerDelegate>

// UnityGLViewController keeps this instance.
@property(nonatomic) UIImagePickerController* pickerController;

@property(nonatomic) NSString *outputFileName;

+ (instancetype)sharedInstance;

- (void)show:(NSString *)title outputFileName:(NSString *)name;

@end
