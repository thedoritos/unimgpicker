//
//  Picker.mm
//  Unity-iPhone
//
//  Created by thedoritos on 11/19/16.
//
//

#import "Picker.h"

#pragma mark Config

const char* CALLBACK_OBJECT = "Unimgpicker";
const char* CALLBACK_METHOD = "OnComplete";
const char* CALLBACK_METHOD_FAILURE = "OnFailure";

const char* MESSAGE_FAILED_PICK = "Failed to pick the image";
const char* MESSAGE_FAILED_FIND = "Failed to find the image";
const char* MESSAGE_FAILED_COPY = "Failed to copy the image";

#pragma mark Picker

@interface Picker()
@end

@implementation Picker

+ (instancetype)sharedInstance {
    static Picker *instance;
    static dispatch_once_t token;
    dispatch_once(&token, ^{
        instance = [[Picker alloc] init];
    });
    return instance;
}

- (void)show:(NSString *)title outputFileName:(NSString *)name {
    if (self.pickerController != nil && self.pickerController.beingPresented) {
        UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD_FAILURE, MESSAGE_FAILED_PICK);
        return;
    }

    self.pickerController = [[UIImagePickerController alloc] init];
    self.pickerController.delegate = self;

    self.pickerController.allowsEditing = NO;
    self.pickerController.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;

    UIViewController *unityController = UnityGetGLViewController();
    [unityController presentViewController:self.pickerController animated:YES completion:^{
        self.outputFileName = name;
    }];
}

#pragma mark UIImagePickerControllerDelegate

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info {
    NSURL *imageURL = info[UIImagePickerControllerImageURL];
    if (imageURL == nil) {
        UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD_FAILURE, MESSAGE_FAILED_FIND);
        [self dismissPicker];
        return;
    }

    NSString *outputPath = [NSTemporaryDirectory() stringByAppendingPathComponent:self.outputFileName];
    NSURL *outputURL = [NSURL fileURLWithPath:outputPath];

    NSError *fileError;
    NSFileManager *fileManager = [NSFileManager defaultManager];

    if ([fileManager fileExistsAtPath:outputPath]) {
        BOOL removed = [[NSFileManager defaultManager] removeItemAtURL:outputURL error:&fileError];
        if (removed == NO) {
            UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD_FAILURE, MESSAGE_FAILED_COPY);
            [self dismissPicker];
            return;
        }
    }

    BOOL copied = [fileManager copyItemAtURL:imageURL toURL:outputURL error:&fileError];
    if (copied == NO) {
        UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD_FAILURE, MESSAGE_FAILED_COPY);
        [self dismissPicker];
        return;
    }

    UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD, [outputPath UTF8String]);

    [self dismissPicker];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD_FAILURE, MESSAGE_FAILED_PICK);

    [self dismissPicker];
}

- (void)dismissPicker
{
    self.outputFileName = nil;

    if (self.pickerController != nil) {
        [self.pickerController dismissViewControllerAnimated:YES completion:^{
            self.pickerController = nil;
        }];
    }
}

@end

#pragma mark Unity Plugin

extern "C" {
    void Unimgpicker_show(const char* title, const char* outputFileName) {
        Picker *picker = [Picker sharedInstance];
        [picker show:[NSString stringWithUTF8String:title] outputFileName:[NSString stringWithUTF8String:outputFileName]];
    }
}
