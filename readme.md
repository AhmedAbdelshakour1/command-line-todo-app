# Command-Line To-Do Application

A command-line to-do application built with Java 21.

### Prerequisites
- **Java 21** or higher
- No external dependencies required! Uses only JDK built-in features

### Installation & Running

```bash
# 1. Download/clone all Java files to a directory
# 2. Open terminal/command prompt in that directory
# 3. Compile all Java files
javac *.java

# 4. Run the application
java TodoApp
```

## 🎯 Features

### Core Requirements ✅
- ✅ **add** - Add new tasks
- ✅ **list** - List all tasks with index numbers
- ✅ **done** - Mark tasks as completed
- ✅ **delete** - Delete tasks
- ✅ **File Storage** - Persistent storage in tasks.txt

### Bonus Features ✅
- ✅ **Timestamps** - Track creation and completion times
- ✅ **Priorities** - High, Medium, Low priority levels
- ✅ **Tags** - Organize tasks with custom tags
- ✅ **Filtering** - Filter by completed/pending status

## 📖 Usage Guide

### Basic Commands

#### Adding Tasks
```bash
# Simple task
todo> add "Buy milk"

# Task with priority
todo> add "Submit report" --priority high

# Task with tags
todo> add "Call dentist" --tags health,appointments

# Task with priority and tags
todo> add "Team meeting prep" --priority medium --tags work,meetings
```

#### Listing Tasks
```bash
# List all tasks
todo> list

# List only pending tasks
todo> list --filter pending

# List only completed tasks
todo> list --filter completed
```

#### Managing Tasks
```bash
# Mark task as completed
todo> done 3

# Delete a task
todo> delete 5

# Get help
todo> help

# Exit application
todo> exit
```