import os
from os import path, makedirs
from sys import argv
from typing import Dict, Tuple, List

from .data_class import DerivedClass, get_base_class

# Package string
ROOT = os.getcwd()
Pair = Tuple[str, str]


def output(source: str) -> None:
    rem_lines = []
    base_n = ""
    imports = ""
    package = ""
    output_dir = path.join(ROOT, "src", "main", "java", "com", "jlox")
    with open(source, "r") as src_file:
        base_n = src_file.readline()
        output_dir = path.join(output_dir, base_n.lower().strip())
        base_n = base_n.strip(" \n")
        lines = src_file.readlines()
        for i, line in enumerate(lines):
            if line.startswith("$"):
                package += line.strip("$\n")
                package += "\n"
            elif line.startswith("*"):
                imports += line.strip("*\n")
                imports += "\n"
            else:
                rem_lines = lines[i:]
                break
    generate_output(output_dir, package, imports, base_n, "Visitor",
                    rem_lines)


def visitor_interface(package: str, vistor_name: str,
                      class_names: List[str]) -> str:
    v_str = f"{package}\n\npublic interface {vistor_name}<R> "
    v_str += "{\n"
    for name in class_names:
        v_str += f"\tpublic R visit{name}({name} {name.lower()});\n"
    v_str += "}"
    return v_str


def generate_output(output_dir: str, package: str, imports: str, base: str,
                    visitor: str, rem_lines: List[str]):
    makedirs(output_dir, exist_ok=True)
    vistor_name = f"{base}{visitor}"
    with open(path.join(output_dir, f"{base}.java"), "w") as base_file:
        base_file.write(get_base_class(base, vistor_name, package))
    class_dict = parse_output(rem_lines)
    for key in class_dict.keys():
        out_path = path.join(output_dir, f"{key}.java")
        expression = DerivedClass(base, key,  package, imports, vistor_name)
        expression.add_fields(class_dict[key])
        with open(out_path, "w") as out_file:
            out_file.write(expression.to_string())
    class_names = [key for key in class_dict.keys()]
    visitor_path = path.join(output_dir, f"{vistor_name}.java")
    with open(visitor_path, "w") as visitor_file:
        visitor_file.write(visitor_interface(package, vistor_name, class_names))


def parse_output(lines: List[str]) -> Dict[str, List[Pair]]:
    parsed_lines = []
    for line in lines:
        parsed_lines.append(parse_line(line))
    return {line[0]: line[1] for line in parsed_lines}


def parse_line(line: str) -> Tuple[str, List[Pair]]:
    words = line.split(" ")
    assert len(words) % 2 != 0
    pairs = [(words[i + 1].strip(), words[i].strip()) for i in
             range(1, len(words), 2)]
    return words[0].strip(), pairs


if __name__ == "__main__":
    if len(argv) == 2:
        output(argv[1])
    else:
        root = f"{os.getcwd()}/tools/packages"
        files = os.listdir(root)
        for fp in files:
            if fp.endswith(".txt"):
                output(f"{root}/{fp}")



