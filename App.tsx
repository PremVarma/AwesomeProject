import React, {useEffect} from 'react';
import {
  SafeAreaView,
  Text,
  NativeEventEmitter,
  NativeModules,
  StyleSheet,
  Alert,
  PermissionsAndroid,
  Linking,
} from 'react-native';
import {
  check,
  request,
  PERMISSIONS,
  RESULTS,
  requestMultiple,
} from 'react-native-permissions';

const {ScreenshotModule} = NativeModules;
const screenshotEvents = new NativeEventEmitter(ScreenshotModule);

const App = () => {
  useEffect(() => {
    askForPermission();
    checkPermission();
    const subscription = screenshotEvents.addListener(
      'screenshotDetected',
      () => {
        console.log('Screenshot detected!');
        Alert.alert('Event Received!');
        // Handle screenshot event here
      },
    );

    const subscriptionInit = screenshotEvents.addListener(
      'mainActivityEvent',
      message => {
        Alert.alert('MainAcitivity Received!');
        console.log(message); // Logs the message emitted when MainActivity is created
      },
    );

    // Clean up the subscription on component unmount
    return () => {
      subscription.remove();
      subscriptionInit.remove();
    };
  }, []);

  const askForPermission = () => {
    // Function to handle permission logic
    requestMultiple([
      PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE,
      PERMISSIONS.ANDROID.READ_MEDIA_IMAGES,
    ]).then(statuses => {
      console.log('Camera', statuses[PERMISSIONS.ANDROID.READ_MEDIA_IMAGES]);
      console.log(
        'FaceID',
        statuses[PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE],
      );
    });
  };

  // Handle the result of the permission request
  const checkPermission = () => {
    check(PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE)
      .then(result => {
        switch (result) {
          case RESULTS.UNAVAILABLE:
            console.log(
              'This feature is not available (on this device / in this context)',
            );
            break;
          case RESULTS.DENIED:
            console.log(
              'The permission has not been requested / is denied but requestable',
            );
            break;
          case RESULTS.LIMITED:
            console.log('The permission is limited: some actions are possible');
            break;
          case RESULTS.GRANTED:
            console.log('The permission is granted');
            break;
          case RESULTS.BLOCKED:
            console.log('The permission is denied and not requestable anymore');
            break;
        }
      })
      .catch((error: any) => {
        // …
        console.log(error);
      });
  };
  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.text}>Screenshot Detection App</Text>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#fff',
  },
  text: {
    fontSize: 18,
    fontWeight: 'bold',
  },
});

export default App;
