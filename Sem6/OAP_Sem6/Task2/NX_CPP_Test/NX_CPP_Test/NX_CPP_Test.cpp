#include <uf.h>
#include <uf_curve.h>
#include <uf_sket.h>
#include <uf_modl.h>
#include <string>



void createExtrude() {
	tag_t Line1Tag = NULL_TAG;
	UF_CURVE_line_t Line_coords1;
	Line_coords1.start_point[0] = 0;
	Line_coords1.start_point[1] = 0;
	Line_coords1.start_point[2] = 0;
	Line_coords1.end_point[0] = 10;
	Line_coords1.end_point[1] = 10;
	Line_coords1.end_point[2] = 0;
	UF_CURVE_create_line(&Line_coords1, &Line1Tag);

	tag_t Line2Tag = NULL_TAG;
	UF_CURVE_line_t Line_coords2;
	Line_coords2.start_point[0] = 0;
	Line_coords2.start_point[1] = 0;
	Line_coords2.start_point[2] = 0;
	Line_coords2.end_point[0] = 0;
	Line_coords2.end_point[1] = 10;
	Line_coords2.end_point[2] = 0;
	UF_CURVE_create_line(&Line_coords2, &Line2Tag);

	tag_t Line3Tag = NULL_TAG;
	UF_CURVE_line_t Line_coords3;
	Line_coords3.start_point[0] = 0;
	Line_coords3.start_point[1] = 10;
	Line_coords3.start_point[2] = 0;
	Line_coords3.end_point[0] = 10;
	Line_coords3.end_point[1] = 10;
	Line_coords3.end_point[2] = 0;
	UF_CURVE_create_line(&Line_coords3, &Line3Tag);


	tag_t sketch1 = NULL_TAG;
	char scn[129] = "scname1";
	UF_SKET_initialize_sketch(scn, &sketch1);
	int option = 2;
	double matrix[9] = { 1,0,0,0,1,0,0,0,0 };
	tag_t object[2];
	int reference[2];
	int plane_dir = 2;
	UF_SKET_create_sketch(scn, option, matrix, object, reference,
		plane_dir, &sketch1);
	tag_t SketchLine[3] = { Line1Tag, Line2Tag, Line3Tag};
	UF_SKET_add_objects(sketch1, 3, SketchLine);

	uf_list_p_t Ext3;
	UF_FEATURE_SIGN extsign3 = UF_NULLSIGN;
	UF_MODL_create_list(&Ext3);
	UF_MODL_put_list_item(Ext3, sketch1);
	char tapang3[5] = { "0.0" };
	char ext3s[15] = "0.0";


	std::string temp3 = std::to_string(15.0 / 2);
	char ext3e[15];
	strcpy_s(ext3e, temp3.c_str());
	char* ExtLimit3[2] = { ext3s, ext3e };
	double extpoint3[3] = { 50.0,0.0,0.0 };
	double extdir3[3] = { 0.0,0.0,1.0 };
	uf_list_p_t ExtFeatureList3;
	UF_MODL_create_extruded(Ext3, tapang3, ExtLimit3, extpoint3,
		extdir3, extsign3, &ExtFeatureList3);
	UF_MODL_delete_list(&ExtFeatureList3);
	UF_MODL_delete_list(&Ext3);


	UF_terminate();
}

void createCircle() {
	double c1point1[3] = { 70.0,0.0,0.0 };
	double c1point2[3] = { 30.0,0.0,0.0 };
	double c1point3[3] = { 50.0,0.0,20.0 };
	tag_t circule1 = NULL;
	UF_CURVE_create_arc_thru_3pts(2, c1point1, c1point2, c1point3,
		&circule1);
}

void createSketchAndALineInIt() {

	tag_t Line1Tag = NULL_TAG;
	UF_CURVE_line_t Line_coords1;
	Line_coords1.start_point[0] = 0;
	Line_coords1.start_point[1] = 0;
	Line_coords1.start_point[2] = 0;
	Line_coords1.end_point[0] = 10;
	Line_coords1.end_point[1] = 10;
	Line_coords1.end_point[2] = 0;
	UF_CURVE_create_line(&Line_coords1, &Line1Tag);

	tag_t sketch1 = NULL_TAG;
	char scn[129] = "scname1";
	UF_SKET_initialize_sketch(scn, &sketch1);
	int option = 2;
	double matrix[9] = { 1,0,0,0,1,0,0,0,0 };
	tag_t object[2];
	int reference[2];
	int plane_dir = 2;
	UF_SKET_create_sketch(scn, option, matrix, object, reference,
		plane_dir, &sketch1);
	tag_t SketchLine[1] = { Line1Tag};
	UF_SKET_add_objects(sketch1, 1, SketchLine);
}



void createRotate() {
	uf_list_p_t REv1;
	UF_MODL_create_list(&REv1);
	UF_MODL_put_list_item(REv1, sketch1);
	char rev1s[15] = "180.0";
	char rev1e[15] = "360.0";
	char* RevLimit[2] = { rev1s, rev1e };
	double revpoint[3] = { 0.0,0.0,0.0 };
	double revdir[3] = { 0.0,1.0,0.0 };
	uf_list_p_t RevFeatureList;
	UF_MODL_create_revolved(REv1, RevLimit, revpoint, revdir, UF_NULLSIGN,
		&RevFeatureList);
	UF_MODL_delete_list(&RevFeatureList);
	UF_MODL_delete_list(&REv1);
}

void ufusr(char* param, int* retcode, int paramLen) {
	UF_initialize();
	
	createExtrude();

	UF_terminate();
}

int ufusr_ask_unload(void) {
	return (UF_UNLOAD_IMMEDIATELY);
}