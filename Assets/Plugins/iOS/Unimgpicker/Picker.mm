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

@implementation Picker

+ (instancetype)sharedInstance {
    static Picker *instance;
    static dispatch_once_t token;
    dispatch_once(&token, ^{
        instance = [[Picker alloc] init];
    });
    return instance;
}

- (void)show:(NSString *)title outputFileName:(NSString *)name maxSize:(NSInteger)maxSize {
    if (self.pickerController != nil) {
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
    UIImage *image = info[UIImagePickerControllerOriginalImage];
    if (image == nil) {
        UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD_FAILURE, MESSAGE_FAILED_FIND);
        [self dismissPicker];
        return;
    }
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    if (paths.count == 0) {
        UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD_FAILURE, MESSAGE_FAILED_COPY);
        [self dismissPicker];
        return;
    }
    
    NSString *imageName = self.outputFileName;
    if ([imageName hasSuffix:@".png"] == NO) {
        imageName = [imageName stringByAppendingString:@".png"];
    }
    
    NSString *imageSavePath = [(NSString *)[paths objectAtIndex:0] stringByAppendingPathComponent:imageName];
    NSData *png = UIImagePNGRepresentation(image);
    if (png == nil) {
        UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD_FAILURE, MESSAGE_FAILED_COPY);
        [self dismissPicker];
        return;
    }
    
    BOOL success = [png writeToFile:imageSavePath atomically:YES];
    if (success == NO) {
        UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD_FAILURE, MESSAGE_FAILED_COPY);
        [self dismissPicker];
        return;
    }
    
    UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD, [imageSavePath UTF8String]);
    
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
    void Unimgpicker_show(const char* title, const char* outputFileName, int maxSize) {
        Picker *picker = [Picker sharedInstance];
        [picker show:[NSString stringWithUTF8String:title] outputFileName:[NSString stringWithUTF8String:outputFileName] maxSize:(NSInteger)maxSize];
    }
}
