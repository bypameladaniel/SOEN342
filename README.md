# SOEN342 Project

### Team members
| Name                 | Student ID | GitHub Profile | Role |
|:--------------------:|:----------:|:--------------:| :--------------:|
| Pamela Daniel       | 40286602    | [bypameladaniel](https://github.com/bypameladaniel) | Team Leader |
| Quinton Shannon     | 40283158    | [qrs-programmer](https://github.com/qrs-programmer) | Member |
| Victor Bruson       | 40284702    | [VictorBruson](https://github.com/VictorBruson) | Member |

## How to Run the Project

You can run the project in two ways: using **Visual Studio Code** or the **terminal**.

---

### 1. Using Visual Studio Code

1. Open the project folder in **Visual Studio Code**.  
2. Make sure the **Java Extension Pack** is installed (it includes essential Java tools).  
3. Open `Main.java` inside the `src` folder.  
4. Click the **Run** button ▶ at the top-right corner of the editor, or press:
   - `Ctrl + F5` on Windows/Linux  
   - `Cmd + F5` on macOS  

The program will start running in the built-in terminal.

---

### 2. Using the Terminal

1. Open your terminal and navigate to the project directory:

   ```bash
   cd SOEN342
2. Compile all Java source files (output goes to the bin directory):

   ```bash
   javac -d bin -cp "lib/*" src/*.java

3. Run the compiled program:

   ```bash
   java -cp "bin:lib/*" Main
   
