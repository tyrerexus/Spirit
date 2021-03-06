#include <stdio.h>
#include <libgen.h>
#include <string.h>
#include <stdlib.h>
#include "grammar.tab.h"
#include "ast.h"
#include "debug/ast_debug.h"
#include "symtbl.h"
#include "compile.h"
#include "types.h"

static void print_version()
{
	printf("Cheri v0.0.1\n");
	printf("© 2017 TYREREXUS ALL RIGHTS RESERVED\n");
}

/**
 * Just prints the help information. (^.^)
 */
static void help()
{
	printf("Usage: cheri [options] files...\n");
	printf("Options:\n");
	printf("    --help        Dispays this help section.\n");
	printf("    -v            Prints out the version\n");
	printf("    -o            Specifies where to place output binary.\n");
	printf("    --out-dir     Specifies where to place build files.\n");
}

int main(int argc, char *args[])
{
	typedef struct FilesList FilesList;
	struct FilesList {
		FilesList *prev;
		char *filename;
	};

	FilesList *files_list = NULL;
	char* output_filename = NULL;

	// FIXME: Add a separate file for this.
	/*** PARSE ARGUMENTS ***/
	for(int arg_index = 1; arg_index < argc; arg_index++) {
		char* arg_to_parse = args[arg_index];
		if (arg_to_parse[0] == '-') {

			/* The output flag. */
			if (strcmp(arg_to_parse, "-o") == 0) {
				arg_index++;
				output_filename = args[arg_index];
			}

			else if (strcmp(arg_to_parse, "-v") == 0) {
				print_version();

				// FIXME: Do proper clean-up maybe...
				return 0;
			}

			/* Set the output dir. */
			else if (strcmp(arg_to_parse, "--out-dir") == 0) {
				arg_index++;
				out_dir = args[arg_index];
			}

			/* The help option. */
			else if (strcmp(arg_to_parse, "-h") == 0 || strcmp(arg_to_parse, "--help") == 0) {
				help();
				return 0;
			}

			/* Show an error. */
			else {
				printf("Unknown switch: %s", arg_to_parse);
				return 1;
			}
		}

		/* If this wasn't an option than we append to list of files to compile. */
		else {
			/* Append to list. */
			FilesList* f = malloc(sizeof(FilesList));
			f->filename = arg_to_parse;
			f->prev = files_list;
			files_list = f;
		}
	}
#ifdef DEBUG
	printf("Output: %s\n", output_filename);
#endif

	/* Open the output file. If none specified use the stdout. */
	out_file = output_filename != NULL ? fopen(output_filename, "w") : stdout;

	/*** INIT THE COMPILER ***/
	global_symbol_table = sym_define("global", NULL, SYM_NONE, NULL);
	init_types();

	/*** PARSE EACH FILE GIVEN AS INPUT ***/
	while(files_list != NULL) {
#if DEBUG
		printf("Parsing: %s\n", files_list->filename);
#endif
		compile_file(files_list->filename);
		FilesList *old = files_list;
		files_list = files_list->prev;
		free(old);

	}

	/*** CLEAN UP ***/
	/* Free compile results. */
	while (current_compile_result != NULL) {
		CompileResult *old = current_compile_result;
		current_compile_result = current_compile_result->prev_result;
		free_compile_result(old);
	}

	/* Destroy parser. */
	extern int yylex_destroy();
	int result_yylex_destroy = yylex_destroy();
	if (result_yylex_destroy != 0) {
		printf("ERROR: Could not destroy yylex. %d\n", result_yylex_destroy);
	}

	return 0;
}
