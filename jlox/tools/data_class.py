from typing import Dict, Tuple, List, Optional


def get_base_class(name: str, visitor: str, package: str) -> str:
    so_far = package + "\n\n"
    so_far += "public abstract class " + name + " {\n"
    so_far += "\tpublic abstract <R> R accept(" + visitor + "<R> v);\n}"
    return so_far


Pair = Tuple[str, str]

def get_field_str(name: str, type_str: str) -> str:
    """Get a string output for a defined field"""
    return f"\tpublic final {type_str} {name};\n"


class DerivedClass:
    def __init__(self, base: str, name: str, package: str, imports: str,
                 visitor: Optional[str] = None) -> None:
        self.fields: Dict[str, str] = {}
        self.__name: str = name.strip()
        self.__base = base.strip()
        self.__visitor = visitor
        self.__package = package
        self.__imports = imports

    @property
    def has_visitor(self) -> bool:

        return self.__visitor is not None

    @property
    def name(self) -> str:
        """Return the name of this class"""
        return self.__name

    @property
    def base(self) -> str:
        """Return the name of the  base class"""
        return self.__base

    @property
    def visitor_name(self) -> str:
        """Return the name of the visitor class"""
        assert self.__visitor is not None
        return self.__visitor

    def add_fields(self, pairs: List[Pair]):
        """Add multiple fields to the generated class. See docs for add_field"""
        for pair in pairs:
            self.add_field(pair)

    def add_field(self, pair: Pair) -> None:
        """
         Add a field to the generated class
         Each fields name is determined by the first value in the pair and the
         type is determined by the second value
         Each field will be set to private
         Getters will be generated for each field
         """
        self.fields[pair[0]] = pair[1]

    def to_string(self) -> str:
        """Generate the class as a string"""
        class_str = self.__package
        if self.__imports != "":
            class_str += f"\n\n{self.__imports}"
        class_str += f"\n\npublic class {self.name} extends {self.base} "
        class_str += "{\n"
        # Add the fields to the class
        for key in self.fields.keys():
            class_str += get_field_str(key, self.fields[key])
        class_str = DerivedClass.add_line(class_str, self.constructor())
        if self.has_visitor:
            class_str = DerivedClass.add_line(
                class_str, self.visitor(f"{self.visitor_name}")
            )
        class_str += "}\n"
        return class_str

    def constructor(self) -> str:
        """Get the generated class constructor"""
        args = [f"{self.fields[key]} {key}, " for key in self.fields.keys()]
        # public Name (int sfa, ..) {
        args_str = "".join(args)
        args_str = args_str.strip(" ,")
        # Add list of params to constructor
        my_str = f"\tpublic {self.name} ({args_str}"
        my_str = my_str + ") {\n"
        # \t this.stuff = stuff;
        for key in self.fields.keys():
            my_str = f"{my_str}\t\tthis.{key} = {key};\n"
        my_str += "\t}\n"
        return my_str

    def visitor(self, visitor_name: str) -> str:
        """Implement the visitor in the generated class"""
        header = "\tpublic <R> R accept(" + visitor_name + "<R> visitor) {\n"
        body = f"\t\treturn visitor.visit{self.name}(this);\n"
        return header + body + "\t}\n"

    @staticmethod
    def add_line(instr, line):
        """A line of string + a new line character"""
        return instr + "\n" + line
