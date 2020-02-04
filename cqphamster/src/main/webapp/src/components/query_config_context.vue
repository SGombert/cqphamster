<template>
    <div class="row">
        <div class="col">
            <q-input
            v-model="val"
            filled
            autogrow
            type="number"
            placeholder="Generated Query"
            style="margin-right:15px;"
            />
        </div>
    </div>
</template>

<style>
  .h-cl {
    text-shadow:0px 0px 5px white;
    text-align:center;
    width:100%;
    margin-left:auto;
    margin-right:auto;
  }

</style>

<script>
export default {
  name: "query_config_context",
  data () {
    return {
      val: '0'
    }
  },
  methods () {
      return {
          inputChange(evt) {

          }
      }
  }
};
</script>