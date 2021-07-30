# InvoidSampleLibrary
## How to: 
> Add this lines in your onclick listener:

```
InvoidVerification invoid = new InvoidVerification();
invoid.verify(CurrentActivity.this, WelcomeActivity.class);
 ```

> Step 1. Add the JitPack repository to your build file

```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
  
  > Step 2. Add the dependency
  
  ```gradle
  dependencies {
	        implementation 'com.github.amit8899:InvoidSampleLibrary:Tag'
	}
 ```
